package com.youthlin.example.annotation;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * @author youthlin.chen
 * @date 2020-07-14 11:36:09
 */
@SupportedAnnotationTypes({
        "com.youthlin.example.annotation.async",
        "com.youthlin.example.annotation.await"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AnnotationProcessor extends AbstractProcessor {
    private Messager messager;
    private JavacTrees trees;
    private TreeMaker treeMarker;
    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        treeMarker = TreeMaker.instance(context);
        names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        debug("annotations=" + annotations);
        new Exception("看下堆栈").printStackTrace();

        processAsync(roundEnv);
        return true;
    }

    private void processAsync(RoundEnvironment roundEnv) {
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(async.class);
        set.forEach(element -> {
            JCTree jcTree = trees.getTree(element);
            debug("element= %s, jcTree= %s", element, jcTree);
            jcTree.accept(new TreeTranslator() {

                @Override
                public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {

                    JCTree.JCExpression returnType = (JCTree.JCExpression) jcMethodDecl.getReturnType();
                    debug("returnType: %s %s", returnType, returnType.getClass());
                    if (returnType.type.isPrimitiveOrVoid()) {
                        returnType = box(returnType);
                    }
                    JCTree.JCIdent ident = treeMarker.Ident(names.fromString(CompletableFuture.class.getSimpleName()));
                    JCTree.JCTypeApply jcTypeApply = treeMarker.TypeApply(ident, List.of(returnType));
                    debug("new return type: %s", jcTypeApply);

                    debug("---body:\n%s", jcMethodDecl.body);

                    jcMethodDecl = treeMarker.MethodDef(
                            jcMethodDecl.mods,
                            jcMethodDecl.name,
                            jcTypeApply,
                            jcMethodDecl.typarams,
                            jcMethodDecl.recvparam,
                            jcMethodDecl.params,
                            jcMethodDecl.thrown,
                            jcMethodDecl.body,
                            jcMethodDecl.defaultValue
                    );
                    debug("---\nafter:%s\n---", jcMethodDecl);
                    super.visitMethodDef(jcMethodDecl);
                }
            });
        });
    }

    private void processAwait(RoundEnvironment roundEnv) {
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(await.class);
        set.forEach(element -> {

        });
    }

    private static void debug(String format, Object... args) {
        System.out.println(String.format(format, args));
    }

    private JCTree.JCIdent box(JCTree primitiveTypeTree) {
        String type = primitiveTypeTree.toString();
        type = PRIMITIVE_MAP.get(type);
        return treeMarker.Ident(names.fromString(type));
    }

    private static final Map<String, String> PRIMITIVE_MAP = new HashMap<>();

    static {
        PRIMITIVE_MAP.put(void.class.getSimpleName(), Void.class.getSimpleName());
        PRIMITIVE_MAP.put(byte.class.getSimpleName(), Byte.class.getSimpleName());
        PRIMITIVE_MAP.put(short.class.getSimpleName(), Short.class.getSimpleName());
        PRIMITIVE_MAP.put(int.class.getSimpleName(), Integer.class.getSimpleName());
        PRIMITIVE_MAP.put(long.class.getSimpleName(), Long.class.getSimpleName());
        PRIMITIVE_MAP.put(float.class.getSimpleName(), Float.class.getSimpleName());
        PRIMITIVE_MAP.put(double.class.getSimpleName(), Double.class.getSimpleName());
        PRIMITIVE_MAP.put(boolean.class.getSimpleName(), Boolean.class.getSimpleName());
    }
}
