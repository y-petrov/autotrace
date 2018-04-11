package ypetrov.javac.plugins;

import java.util.HashMap;
import java.util.Map;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreeScanner;

/**
 * The utility class solves single task - finds all <code>return</code> nodes in the block and returns them with their parent nodes
 * 
 * @author yp
 *
 */
public class RetFinder extends TreeScanner<Void, Void> {

	private CompilationUnitTree cut = null;

	public RetFinder(CompilationUnitTree pCut) {
		cut = pCut;
	}

	private Map<ReturnTree, Tree> rets = new HashMap<>();

	@Override
	public Void visitReturn(ReturnTree pNode, Void pParms) {
		Tree papa = TreePath.getPath(cut, pNode).getParentPath().getLeaf();
		rets.put(pNode, papa);
		return super.visitReturn(pNode, pParms);
	}

	public Map<ReturnTree, Tree> getRets(Tree pNode) {
		this.scan(pNode, null);
		return rets;
	}

}
