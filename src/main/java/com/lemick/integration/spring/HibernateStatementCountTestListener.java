package com.lemick.integration.spring;


import com.lemick.api.AssertHibernateSQLStatementCount;
import com.lemick.assertions.HibernateStatementAssertionResult;
import com.lemick.assertions.HibernateStatementAssertionResults;
import com.lemick.integration.hibernate.HibernateStatementCountInspector;
import com.lemick.integration.hibernate.HibernateStatistics;
import jakarta.persistence.EntityManager;
import org.springframework.core.Ordered;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.transaction.TestTransaction;

import java.util.List;
import java.util.function.Supplier;

import static com.lemick.assertions.HibernateStatementAssertionResult.StatementType.*;

public class HibernateStatementCountTestListener implements TestExecutionListener, Ordered {

    private Supplier<HibernateStatistics> statisticsSupplier = HibernateStatementCountInspector::getStatistics;
    private Supplier<Boolean> transactionAvailabilitySupplier = TestTransaction::isActive;


    @Override
    public void beforeTestMethod(TestContext testContext) {
        statisticsSupplier.get().resetStatistics();
    }

    @Override
    public void afterTestMethod(TestContext testContext) {
        AssertHibernateSQLStatementCount annotation = testContext.getTestMethod().getAnnotation(AssertHibernateSQLStatementCount.class);
        if (annotation != null) {
            flushExistingPersistenceContext(testContext, transactionAvailabilitySupplier);
            doStatementCountEvaluation(annotation);
        }
    }


    /**
     * Low precedence for executing before {@link org.springframework.test.context.transaction.TransactionalTestExecutionListener}
     * closes the transaction and have the ability to flush the EntityManager
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private void flushExistingPersistenceContext(TestContext testContext, Supplier<Boolean> transactionAvailabilitySupplier) {
        if (transactionAvailabilitySupplier.get()) {
            testContext.getApplicationContext()
                    .getAutowireCapableBeanFactory()
                    .getBean(EntityManager.class)
                    .flush();
        }
    }

    private void doStatementCountEvaluation(AssertHibernateSQLStatementCount annotation) {
        HibernateStatementAssertionResults assertionResults = new HibernateStatementAssertionResults(List.of(
                new HibernateStatementAssertionResult(SELECT, statisticsSupplier.get().getSelectStatements(), annotation.selects()),
                new HibernateStatementAssertionResult(UPDATE, statisticsSupplier.get().getUpdateStatements(), annotation.updates()),
                new HibernateStatementAssertionResult(INSERT, statisticsSupplier.get().getInsertStatements(), annotation.inserts()),
                new HibernateStatementAssertionResult(DELETE, statisticsSupplier.get().getDeleteStatements(), annotation.deletes())
        ));
        assertionResults.validate();
    }

}
