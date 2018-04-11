package ypetrov.javac.plugins;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCExpressionStatement;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCModifiers;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

/**
 * Utility class. Contains convenience methods for building various trees.
 * 
 * @author yp
 *
 */
public class TraceMaker {

	private static final String LOGGER_VAR_NAME = "__l";

	private TreeMaker tm = null;
	private Names names = null;

	public TraceMaker(TreeMaker pTm, Names pNames) {
		tm = pTm;
		names = pNames;
	}

	/** Creates the expression statement tree of the code:<br/>
	 * <code><i>LOGGER_VAR_NAME</i>.entering(<i>pClzName</i>, <i>pMethName</i>);</code>
	 * @param pClzName - class name
	 * @param pMethName - method name
	 * @return
	 */
	public JCExpressionStatement makeEnteringStmt(String pClzName, String pMethName) {
		JCExpressionStatement retVal = null;

		JCExpression fEntering = makeFieldAccess(LOGGER_VAR_NAME + ".entering");
		JCMethodInvocation call = tm.Apply(List.nil(), fEntering, List.of(tm.Literal(pClzName), tm.Literal(pMethName)));
		call.pos = -1;

		retVal = tm.Exec(call);
		retVal.pos = -1;

		return retVal;
	}

	/** Creates the expression statement tree of the code:<br/>
	 * <code><i>LOGGER_VAR_NAME</i>.exiting(<i>pClzName</i>, <i>pMethName</i>);</code>
	 * @param pClzName - class name
	 * @param pMethName - method name
	 * @return
	 */
	public JCExpressionStatement makeExitingStmt(String pClzName, String pMethName) {
		JCExpressionStatement retVal = null;

		JCExpression fExiting = makeFieldAccess(LOGGER_VAR_NAME + ".exiting");
		JCMethodInvocation call = tm.Apply(List.nil(), fExiting, List.of(tm.Literal(pClzName), tm.Literal(pMethName)));
		call.pos = -1;
		
		retVal = tm.Exec(call);
		retVal.pos = -1;

		return retVal;
	}
	
	/** Getting <code>return</code> statement and method's attributes, builds the block<br>
	 * <code>{<br/>
	 * <i>LOGGER_VAR_NAME</i>.exiting(...);<br/>
	 * return ...;<br/>
	 * }</code><br/>
	 * @param pRetStmt
	 * @param pClzName
	 * @param pMethName
	 * @return
	 */
	public JCBlock makeReturnBlock(JCStatement pRetStmt, String pClzName, String pMethName) {
		JCBlock retVal = null;
		
		retVal = tm.Block(0, //
				List.of( //
						makeExitingStmt(pClzName, pMethName), //
						pRetStmt //
						)
				);
		retVal.pos = -1;
		return retVal;
	}

	/**
	 * Makes var-decl tree for<br/>
	 * <code>private static java.util.logging.Logger <i>LOGGER_VAR_NAME</i> = java.util.logging.Logger.getLogger(<i>logger-name-string</i>)</code>
	 * 
	 * @param pLoggerName
	 *            - the name for the logger
	 * @return
	 */
	public JCVariableDecl makeLogVar(String pLoggerName) {
		JCVariableDecl retVal = null;
		JCModifiers logVarModifiers = tm.Modifiers(Flags.PUBLIC /*PRIVATE*/ | Flags.STATIC);
		Name logVarName = names.fromString(LOGGER_VAR_NAME);
		JCExpression logVarType = makeFieldAccess("java.util.logging.Logger");
		JCExpression logVarInit = tm.Apply(List.nil(), makeFieldAccess("java.util.logging.Logger.getLogger"), List.of(tm.Literal(pLoggerName)));
		logVarInit.pos = -1;
		retVal = tm.VarDef(logVarModifiers, logVarName, logVarType, logVarInit);
		retVal.pos = -1;
		return retVal;
	}

	/**
	 * Build JCFieldAccess tree from qualified field name e.g. "java.util.logging.Logger" or "__l.entering"
	 * 
	 * @param pName
	 * @return
	 */
	public JCExpression makeFieldAccess(String pName) {
		JCExpression retVal = null;
		if (pName != null) {
			String cleanStr = pName.replaceAll("[ \\t]", "");
			if (cleanStr.length() > 0) {
				String[] parts = cleanStr.split("\\.");
				if (parts.length > 0) {
					JCIdent top = tm.Ident(names.fromString(parts[0]));
					top.pos = -1;
					JCExpression papa = top;
					for (int i = 1; i < parts.length; i++) {
						papa = tm.Select(papa, names.fromString(parts[i]));
						papa.pos = -1;
					}
					retVal = papa;
				}
			}
		}
		return retVal;
	}

}
