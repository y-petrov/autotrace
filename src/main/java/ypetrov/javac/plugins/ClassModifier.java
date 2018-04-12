package ypetrov.javac.plugins;

import java.util.List;
import java.util.Map;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCCase;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCIf;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Names;

public class ClassModifier extends TreeScanner<Void, Void> {

	private Context ctx = null;
	private CompilationUnitTree cut = null;
	private TreeMaker tm = null;
	private Names names = null;
	private Log logger = null;
	private String pkgName = null;

	public ClassModifier(Context pCtx, CompilationUnitTree pCut) {
		ctx = pCtx;
		cut = pCut;

		tm = TreeMaker.instance(ctx);
		names = Names.instance(ctx);
		logger = Log.instance(ctx);
		pkgName = cut.getPackageName() == null ? "<default>" : cut.getPackageName().toString();
		return;
	}

	/**
	 * <ol>
	 * <li>Creates logger variable</li>
	 * <li>Traverses the class's methods and init blocks and augmets them win enter/exit logging</li>
	 * </ol>
	 * We need to get all methods including constructors and initializer blocks (non-Javadoc)
	 * 
	 * @see com.sun.source.util.TreeScanner#visitClass(com.sun.source.tree.ClassTree, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Void visitClass(ClassTree pClsNode, Void pParms) {
		if (isAutotraceable(pClsNode)) {
			List<? extends Tree> membLst = pClsNode.getMembers();

			TraceMaker logMaker = new TraceMaker(tm, names);

			// The logger var is created only in the classes on the top of nesting tree
			if (TreePath.getPath(cut, pClsNode).getParentPath().getLeaf() instanceof CompilationUnitTree) {
				JCStatement logVarDecl = logMaker.makeLogVar((new StringBuilder(pkgName)).append(".").append(((JCClassDecl) pClsNode).name).toString());
				@SuppressWarnings("rawtypes")
				List withLogVar = Util.addOne(logVarDecl, membLst, 0, true);
				((JCClassDecl) pClsNode).defs = (com.sun.tools.javac.util.List<JCTree>) withLogVar;
			}

			StringBuilder sb = new StringBuilder();

			BlockTree blockToAugm = null;
			String clzName = makeClzName4Print(pClsNode);
			for (Tree member : membLst) {

				if (member != null) {
					switch (member.getKind()) {
					case BLOCK:
						blockToAugm = (BlockTree) member;
						boolean isStat = ((JCBlock) blockToAugm).isStatic();
						sb.delete(0, sb.length());
						augmBlock(blockToAugm, logMaker, clzName, sb.append("<").append(isStat ? "static" : "instance").append(" init>").toString());
						break;
					case METHOD:
						blockToAugm = ((MethodTree) member).getBody();
						sb.delete(0, sb.length());
						augmBlock(blockToAugm, logMaker, clzName, sb.append(((MethodTree) member).getName().toString()).toString());
						break;
					default:
						break;
					}
				}

			}
		} else {
			logger.rawWarning(((JCClassDecl) pClsNode).pos, "No Autotrace for non-static inner classes");
		}

		return super.visitClass(pClsNode, pParms);
	}

	/**
	 * Augments the body block of a method or int with "entering" log before the 1st block's statement and with one or more "exiting" logs depending on the
	 * block
	 *
	 * @param pBl
	 *            - method's or init's body
	 * @param pTm
	 * @param pClzName
	 * @param pMethName
	 *            - the block name
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void augmBlock(BlockTree pBl, TraceMaker pTm, String pClzName, String pMethName) {
		List stmtLst = pBl.getStatements();
		int numStmts = stmtLst.size();
		if (numStmts > 0) {
			JCStatement logEnter = pTm.makeEnteringStmt(pClzName, pMethName);

			// Trivial cases - 1st and last statements in the block
			StatementTree stmtLast = (StatementTree) stmtLst.get(numStmts - 1);

			// Now let's visit all nested blocks and collect returns in these blocks
			RetFinder rf = new RetFinder(cut);
			Map<ReturnTree, Tree> allRets = rf.getRets(pBl); // scan(pBl, null);

			// By now we have:
			// - the list of the block's subtrees
			// - [potentially empty] list of ret statements without wrapping blocks. If list is not empty, it's sorted by ret position.
			// - [potentially 'null'] last statement. btw, it may be block with 'return' as block's last statement.

			((JCBlock) pBl).stats = (com.sun.tools.javac.util.List<JCStatement>) Util.addOne((JCStatement) logEnter, stmtLst, 0, true);
			stmtLst = pBl.getStatements();

			JCBlock retBlock = null;
			for (ReturnTree rt : allRets.keySet()) {
				Tree bt = allRets.get(rt);
				if (bt instanceof JCBlock) {
					retBlock = pTm.makeReturnBlock((JCStatement) rt, pClzName, pMethName);
					((JCBlock) bt).stats = (com.sun.tools.javac.util.List<JCStatement>) Util.replace(((JCBlock) bt).stats, rt, retBlock);
				} else if (bt instanceof JCIf) {
					retBlock = pTm.makeReturnBlock((JCStatement) rt, pClzName, pMethName);
					if (((JCIf) bt).thenpart == rt)
						((JCIf) bt).thenpart = retBlock;
					else
						((JCIf) bt).elsepart = retBlock;
				} else if (bt instanceof JCCase) {
					retBlock = pTm.makeReturnBlock((JCStatement) rt, pClzName, pMethName);
					((JCCase) bt).stats = com.sun.tools.javac.util.List.of(retBlock);
				} else {
				}
			}

			// If neither of return-s is the last statement (void method without explicit return at the end), then add one more 'exiting'
			if (!allRets.keySet().contains(stmtLast)) {
				JCStatement logExit = pTm.makeExitingStmt(pClzName, pMethName);
				((JCBlock) pBl).stats = (com.sun.tools.javac.util.List<JCStatement>) Util.addOne(logExit, stmtLst, stmtLst.size() - 1, false);
			}

		} else {
			logger.rawWarning(((JCBlock) pBl).pos, "No Autotrace for empty block");
		}
		return;
	}

	private boolean isAutotraceable(ClassTree pCT) {
		boolean retVal = true;
		/*
		 * Tree papa = TreePath.getPath(cut, pCT).getParentPath().getLeaf(); retVal = papa instanceof CompilationUnitTree; if (!retVal) { // The parent node of
		 * this class is not the file, so pCt is inner class retVal = (((JCClassDecl) pCT).mods.flags & Modifier.STATIC) != 0; }
		 */
		return retVal;
	}

	/**
	 * Makes class name for logging - handles named and anonymous inner classes in addition to the top level ones. Relies on the
	 * <code>CompilationUnitTree</code> object referenced by instance variable and on pakage name instance var.<br />
	 * 
	 * TODO Do something if multiple anonymous classes are in the wrapping class
	 * 
	 * @param pClz
	 *            class tree object
	 * @return
	 */
	private String makeClzName4Print(ClassTree pClz) {
		StringBuilder retVal = new StringBuilder();
		Tree currNode = pClz;
		String currClzName = null;
		while (!(currNode instanceof CompilationUnitTree)) {
			if (currNode instanceof ClassTree) {
				if (currClzName != null)
					retVal.insert(0, "$");
				currClzName = ((JCClassDecl) currNode).getSimpleName().toString();
				if ("".equals(currClzName))
					currClzName = "<anonymous>";
				retVal.insert(0, currClzName);
			}
			currNode = TreePath.getPath(cut, currNode).getParentPath().getLeaf();
		}
		retVal.insert(0, ".").insert(0, pkgName);
		return retVal.toString();
	}
}
