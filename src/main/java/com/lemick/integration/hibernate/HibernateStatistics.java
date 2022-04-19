package com.lemick.integration.hibernate;

import java.util.ArrayList;
import java.util.List;

public class HibernateStatistics implements HibernateStatementCountListener{

    private final List<String> selectStatements = new ArrayList<>();
    private final List<String> updateStatements = new ArrayList<>();
    private final List<String> insertStatements = new ArrayList<>();
    private final List<String> deleteStatements = new ArrayList<>();

    public void resetStatistics() {
        selectStatements.clear();
        updateStatements.clear();
        insertStatements.clear();
        deleteStatements.clear();

    }

    @Override
    public void notifySelectStatement(String sql) {
        selectStatements.add(sql);
    }

    @Override
    public void notifyUpdateStatement(String sql) {
        updateStatements.add(sql);
    }

    @Override
    public void notifyInsertStatement(String sql) {
        insertStatements.add(sql);
    }

    @Override
    public void notifyDeleteStatement(String sql) {
        deleteStatements.add(sql);
    }

    public List<String> getSelectStatements() {
        return selectStatements;
    }

    public List<String> getUpdateStatements() {
        return updateStatements;
    }

    public List<String> getInsertStatements() {
        return insertStatements;
    }

    public List<String> getDeleteStatements() {
        return deleteStatements;
    }
}
