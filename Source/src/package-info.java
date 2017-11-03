/*! \mainpage notitle
 * 
 * This repository contains a large number of simple coding examples illustrating
 * the use of the <a href="http://liflab.github.io/beepbeep-3">BeepBeep 3</a>
 * event stream processor and the various
 * <a href="http://liflab.github.io/beepbeep-3-palettes">palettes</a> that
 * come with it. All the code examples are heavily documented, with line-by-line
 * explanations of what is being done.
 * 
 * \section whatis What is BeepBeep?
 * 
 * If you don't know this yet, please first have a look at
 * <a href="http://liflab.github.io/beepbeep-3">BeepBeep's website</a>.
 * The documentation here only covers the code examples. Please refer to
 * BeepBeep's
 * <a href="http://liflab.github.io/beepbeep-3/javadoc/index.html">API reference</a>
 * to get information about BeepBeep's various objects and their usage.
 *
 * \section usage How to use this repository
 * 
 * The source code repository is organized into Java <a href="namespaces.html">packages</a>.
 * Each package is centered on a particular concept or functionality. For
 * example, the {@link basic} package contains code examples using BeepBeep's
 * core processors, while the {@link plots} package shows how to use
 * processors and functions from the <code>Plots</code> palette.
 * <p>
 * Typically, an example is a file that contains a <code>main()</code>
 * method that you can run. For each file, you can see the detailed
 * source code directly from this website; this can be done by clicking on
 * the link that is found at the bottom of each page. See for example the
 * <a href="_piping_unary_8java_source.html">source code</a> for one of the
 * simplest examples. 
 * 
 * \section run How to run the examples
 * 
 * The easiest way to run the examples is to open this whole repository into
 * your favorite IDE, such as <a href="https://eclipse.org/">Eclipse</a> or
 * <a href="https://netbeans.org/">NetBeans</a>. Simply locate a file with a
 * <code>main()</code> method, read the associated comments in this file to
 * understand what happens, and click <i>Run</i>.
 * <p>
 * All these examples require at least BeepBeep's core library,
 * <code>beepbeep-3.jar</code>, in order to run. The latest release of this
 * library can be <a href="https://github.com/liflab/beepbeep-3/releases">downloaded
 * from GitHub</a>. Many sections of the repository
 * focus on the use of one or more <em>palettes</em> and require additional JAR
 * files. A <a href="https://github.com/liflab/beepbeep-3-palettes/releases">big bundle</a>
 * of all these files can also be downloaded from GitHub.
 * <p>
 * If Ant is installed on your system, typing
 * <pre>
 * $ ant download-deps
 * </pre>
 * from the root folder of this repository should take care of downloading and
 * extracting all the required files and place them in the <code>Source/dep</code>
 * folder. 
 */
