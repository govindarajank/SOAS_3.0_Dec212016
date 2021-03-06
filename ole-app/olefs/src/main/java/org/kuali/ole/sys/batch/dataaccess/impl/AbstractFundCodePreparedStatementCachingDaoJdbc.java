/*
 * Copyright 2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.ole.sys.batch.dataaccess.impl;

import org.kuali.ole.docstore.common.util.DataSource;
import org.kuali.ole.sys.batch.dataaccess.FundCodePreparedStatementCachingDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractFundCodePreparedStatementCachingDaoJdbc implements FundCodePreparedStatementCachingDao {
    protected static final String RETRIEVE_PREFIX = "retrieve-";
    protected static final String INSERT_PREFIX = "insert-";
    protected static final String UPDATE_PREFIX = "update-";
    private static final Logger LOG = LoggerFactory.getLogger(AbstractFundCodePreparedStatementCachingDaoJdbc.class);
    private Connection connection = null;
    private DataSource dataSource = null;

    protected abstract class JdbcWrapper<T> {
        protected abstract void populateStatement(PreparedStatement preparedStatement) throws SQLException;

        void update(Class<T> type, PreparedStatement preparedStatement) {
            try {
                populateStatement(preparedStatement);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("AbstractUpdatingPreparedStatementCachingDaoJdbc.UpdatingJdbcWrapper encountered exception during getObject method for type: " + type, e);
            }
        }
    }

    protected abstract class RetrievingJdbcWrapper<T> extends JdbcWrapper {
        protected abstract T extractResult(ResultSet resultSet) throws SQLException;

        public T get(Class<T> type) {
            T value = null;
            PreparedStatement statement = preparedStatementCache.get(RETRIEVE_PREFIX + type);
            try {
                populateStatement(statement);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    value = extractResult(resultSet);
                    if (resultSet.next()) {
                        throw new RuntimeException("More that one row returned when selecting by primary key in AbstractRetrievingPreparedStatementCachingDaoJdbc.RetrievingJdbcWrapper for: " + type);
                    }
                }
                resultSet.close();
            } catch (SQLException e) {
                throw new RuntimeException("AbstractRetrievingPreparedStatementCachingDaoJdbc.RetrievingJdbcWrapper encountered exception during getObject method for type: " + type, e);
            }
            return (T) value;
        }
    }

    /**
     * Retrieve list jdbc objects
     */
    protected abstract class RetrievingListJdbcWrapper<T> extends JdbcWrapper {
        protected abstract T extractResult(ResultSet resultSet) throws SQLException;

        public List<T> get(Class<T> type) {
            List<T> resultList = new ArrayList<T>();
            PreparedStatement statement = preparedStatementCache.get(RETRIEVE_PREFIX + type);
            try {
                populateStatement(statement);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    resultList.add(extractResult(resultSet));
                }
                resultSet.close();
            } catch (SQLException e) {
                throw new RuntimeException("AbstractRetrievingPreparedStatementCachingDaoJdbc.RetrievingListJdbcWrapper encountered exception during getObject method for type: " + type, e);
            }
            return resultList;
        }
    }

    protected abstract class InsertingJdbcWrapper<T> extends JdbcWrapper {
        public void execute(Class<T> type) {
            update(type, preparedStatementCache.get(INSERT_PREFIX + type));
        }
    }

    protected abstract class UpdatingJdbcWrapper<T> extends JdbcWrapper {
        public void execute(Class<T> type) {
            update(type, preparedStatementCache.get(UPDATE_PREFIX + type));
        }
    }

    protected Map<String, PreparedStatement> preparedStatementCache;

    protected abstract Map<String, String> getSql();




}
