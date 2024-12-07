package org.example;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;

import java.util.HashMap;
import java.util.Optional;

public class ComplexiteCyclomatique extends VoidVisitorWithDefaults<Void> {

    @Override
    public void visit(CompilationUnit unit, Void arg) {
        for(TypeDeclaration<?> type : unit.getTypes()) {
            type.accept(this, null);
        }
    }

    @Override
    public void visit(PackageDeclaration packageDeclaration, Void arg) {
        super.visit(packageDeclaration, null);
        System.out.print("package : '" + packageDeclaration.getName()+ "'");
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
        super.visit(declaration, arg);

        System.out.print("class : '" + declaration.getName()+ "'\n");
        for(MethodDeclaration method : declaration.getMethods()) {
            method.accept(this, arg);
        }

    }

    private Integer complexiteCyclomatique;

    @Override
    public void visit(MethodDeclaration declaration, Void arg) {
        complexiteCyclomatique = 0;
        String methodName = String.valueOf(declaration.getName());
        System.out.print("\tMéthodes : " +methodName );
        Optional<BlockStmt> opt_body = declaration.getBody();

        if(opt_body.isPresent()){
            BlockStmt body = opt_body.get();
            complexiteCyclomatique++;
            body.accept(this, null);
        }
        System.out.println(" complexite : " + complexiteCyclomatique);
    }

    @Override
    public void visit(BlockStmt blockStmt, Void arg) {
        for(Statement stmt : blockStmt.getStatements()){
            stmt.accept(this, null);
        }
    }

    @Override
    public void visit(IfStmt ifStmt, Void arg) {
        complexiteCyclomatique++;
        ifStmt.getThenStmt().accept(this, null);

        if(ifStmt.hasElseBranch()){
            complexiteCyclomatique++;
            ifStmt.getElseStmt().get().accept(this, null);
        }
    }

    @Override
    public void visit(ForStmt forStmt, Void arg){
        complexiteCyclomatique++;
        forStmt.getBody().accept(this, null);
    }

    @Override
    public void visit(WhileStmt whileStmt, Void arg){
        complexiteCyclomatique++;
        whileStmt.getBody().accept(this, null);
    }

    @Override
    public void visit(TryStmt tryStmt, Void arg){
        complexiteCyclomatique++;
        Optional<BlockStmt> optFinally = tryStmt.getFinallyBlock();
        if(optFinally.isPresent())
            complexiteCyclomatique++;
        tryStmt.getCatchClauses().stream()
                .forEach(catchClause -> {
                    complexiteCyclomatique++;
                    catchClause.getBody().accept(this, null);
                });
    }

    @Override
    public void visit(SwitchStmt switchStmt, Void arg){
        for (SwitchEntry entry : switchStmt.getEntries()) {
            for(Statement stmt : entry.getStatements()){
                stmt.accept(this, null);
            }
        }
    }

}