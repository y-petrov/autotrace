package ypetrov.javac.plugins;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.sun.tools.javac.tree.JCTree.JCStatement;

/**
 * The collection of utility static methods
 * 
 * @author yp
 *
 */
public class Util {

	/**
	 * Getting the list, inserts new item at given position. The sad this is that <code>com.sun.tools.javac.util.List</code> is unmutable implementation of
	 * <code>java.util.List</code>. <br/>
	 * No check for <code>null</code>-s is performed.
	 * 
	 * @param pStmt
	 *            - the statement to be in serted into the list
	 * @param pLst
	 *            - the list
	 * @param pPos
	 *            - insertion position
	 * @param pBefore
	 *            - flag that controls the insertion before/after <code>pPos</code>
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List addOne(JCStatement pStmt, List pLst, int pPos, boolean pBefore) {
		List retVal = null;
		LinkedList tmpLst = new LinkedList(pLst);
		tmpLst.add(pBefore ? pPos : (pPos + 1), pStmt);
		retVal = com.sun.tools.javac.util.List.from(tmpLst);
		return retVal;
	}

	/**
	 * Getting the list <code>pLst</code>, replaces the item <code>pOld</code> with <code>pNew</code>. As with {@link addOne}, the reason why this method is
	 * written is that <code>com.sun.tools.javac.util.List</code> is unmutable implementation of <code>java.util.List</code>. <br/>
	 * No check for <code>null</code>-s is performed.
	 * 
	 * @param pLst
	 *            - the list
	 * @param pOld
	 *            - the item to be replaced
	 * @param pNew
	 *            - the replacement
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List replace(List pLst, Object pOld, Object pNew) {
		List retVal = pLst;
		List tmpLst = (List) pLst.stream().map(o -> o == pOld ? pNew : o).collect(Collectors.toList());
		retVal = com.sun.tools.javac.util.List.from(tmpLst);
		return retVal;
	}

}
