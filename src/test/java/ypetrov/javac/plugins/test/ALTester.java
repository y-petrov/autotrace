package ypetrov.javac.plugins.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ypetrov.javac.plugins.AutoTracerPlugin;

public class ALTester {

	static JavaCompiler javac = null;

	// Assuming that the test's working directory is the project root...
	public static final String TEST_CLASSES_ROOT = Paths.get("src", "test", "resources").toAbsolutePath().toString();

	static StandardJavaFileManager fileManager = null;
	static URLClassLoader ucl = null;

	@BeforeClass
	public static void setup() throws Exception {
		javac = ToolProvider.getSystemJavaCompiler();

		fileManager = javac.getStandardFileManager(null, null, null);
		ucl = URLClassLoader.newInstance(new URL[] { new URL("file://" + TEST_CLASSES_ROOT + "/") }, null);
	}

	@Test
	public void testSimple() throws Exception {
		String testClassName = "test.test.subpkg.yp.Test01";

		// 1. Compile
		boolean javacStatus = compileClass(testClassName);
		assertTrue("Compilation failed", javacStatus);

		// 2. Tune logger
		tuneLogger(testClassName);
		
		// 3. Run
		runClass(testClassName, "main");
	}

	@Test
	public void testInners() throws Exception {
		String testClassName = "test.test.subpkg.yp.Test02";

		// 1. Compile
		boolean javacStatus = compileClass(testClassName);
		assertTrue("Compilation failed", javacStatus);

		// 2. Tune logger
		tuneLogger(testClassName);
		
		// 3. Run
		runClass(testClassName, "main");
	}

	@Test
	public void testDefaultPkg() throws Exception {
		String testClassName = "Test03";

		// 1. Compile
		boolean javacStatus = compileClass(testClassName);
		assertTrue("Compilation failed", javacStatus);

		// 2. Tune logger
		tuneLogger("<default>." + testClassName);
		
		// 3. Run
		runClass(testClassName, "main");
	}

	@AfterClass
	public static void bye() {
		try {
			fileManager.close();
		} catch (IOException e) {
			// What can I do?...
			System.err.println(e);
		}
	}

	/**
	 * Does the following:
	 * <ol>
	 * <li>Sets the logging level for the tested class</li>
	 * <li>Adds and tunes console handler</li>
	 * <li>Adds and new formatter</li>
	 * </ol>
	 * 
	 * @param pLoggerName
	 */
	private void tuneLogger(String pLoggerName) {
		Logger l = Logger.getLogger(pLoggerName);
		l.setLevel(Level.FINEST);
		Handler h = new ConsoleHandler();
		h.setLevel(Level.ALL);
		Formatter f = new Formatter() {

			@Override
			public String format(LogRecord lr) {
				String msgTxt = MessageFormat.format(lr.getMessage(), lr.getParameters());
				String txt = MessageFormat.format("{0,date, short} {0,time, full} {1, number, #} {2} {3} {4} {5}\n", lr.getMillis(), lr.getThreadID(),
						lr.getLevel(), lr.getSourceClassName(), lr.getSourceMethodName(), msgTxt);
				return txt;
			}
		};
		h.setFormatter(f);
		l.addHandler(h);
	}

	private boolean compileClass(String classFqn) {
		File[] files = new File[] { new File(TEST_CLASSES_ROOT + "/" + classFqn.replaceAll("\\.", "/") + ".java") }; // input for first compilation task
		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files));
		boolean retVal = javac
				.getTask(null, fileManager, null, Arrays.asList(new String[] { "-Xplugin:" + AutoTracerPlugin.PLUGIN_NAME }), null, compilationUnits).call();
		return retVal;
	}

	/** The method <b><u>must</u></b> take the only argument - <code>String[]</code>.
	 * @param classFqn
	 * @param mName
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void runClass(String classFqn, String mName) throws Exception {
		Class tCz = ucl.loadClass(classFqn);
		Object tObj = tCz.newInstance();
		Method m = tCz.getMethod(mName, String[].class);
		m.invoke(tObj, new Object[] { new String[0] });
	}

}
