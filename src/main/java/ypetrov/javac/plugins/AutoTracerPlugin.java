package ypetrov.javac.plugins;

import com.sun.source.tree.TreeVisitor;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskEvent.Kind;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.util.Context;

public class AutoTracerPlugin implements Plugin, TaskListener {
	
	public static final String PLUGIN_NAME = "AutoTracerPlugin";
	
	Context ctx = null;

	// === Plugin Methods ===
	/**
	 * @see com.sun.source.util.Plugin#getName()
	 */
	public String getName() {
		return PLUGIN_NAME;
	}

	/**
	 * @see com.sun.source.util.Plugin#init(com.sun.source.util.JavacTask, java.lang.String[])
	 */
	public void init(JavacTask task, String... args) {
		task.addTaskListener(this);
		ctx = ((BasicJavacTask) task).getContext();
	}

	// === Task Listener methods ===
	/**
	 * @see com.sun.source.util.TaskListener#started(com.sun.source.util.TaskEvent)
	 */
	public void started(TaskEvent pTe) {
		return;
	}

	/**
	 * Catches the moment when AST (abstract syntax tree) of the compilation unit is built and is ready for tweaking. (non-Javadoc)
	 * 
	 * @see com.sun.source.util.TaskListener#finished(com.sun.source.util.TaskEvent)
	 */
	public void finished(TaskEvent pTe) {
		if (pTe.getKind() == Kind.PARSE) {
			
			// The object 'tv' will visit each class in the compilation unit's tree and modify their trees
			TreeVisitor<Void, Void> tv = new ClassModifier(ctx, pTe.getCompilationUnit());
			pTe.getCompilationUnit().accept(tv, null);
		}
		return;
	}
}