package digit.repository.querybuilder;

import digit.repository.querybuilder.BirthApplicationQueryBuilder;
import digit.web.models.BirthApplicationSearchCriteria;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BirthApplicationQueryBuilderTest {

    @Test
    void testGetBirthApplicationSearchQuery_WithValidCriteria() {
        // Arrange
        BirthApplicationSearchCriteria criteria = new BirthApplicationSearchCriteria();
        criteria.setTenantId("tenant123");
        criteria.setStatus("APPROVED");
        criteria.setIds(Arrays.asList("id1", "id2"));
        criteria.setApplicationNumber("APP123");

        List<Object> preparedStmtList = new ArrayList<>();

        // Act
        BirthApplicationQueryBuilder queryBuilder = new BirthApplicationQueryBuilder();
        String query = queryBuilder.getBirthApplicationSearchQuery(criteria, preparedStmtList);

        // Assert
        String expectedQuery = " SELECT ... FROM ... WHERE btr.tenantid = ? AND btr.status = ? AND btr.id IN (?, ?) AND btr.applicationnumber = ? ORDER BY btr.createdtime DESC";
        assertEquals(expectedQuery, query);
    }

    @Test
    void testGetBirthApplicationSearchQuery_WithNullCriteria() {
        // Arrange
        BirthApplicationSearchCriteria criteria = null;
        List<Object> preparedStmtList = new ArrayList<>();

        // Act
        BirthApplicationQueryBuilder queryBuilder = new BirthApplicationQueryBuilder();
        String query = queryBuilder.getBirthApplicationSearchQuery(criteria, preparedStmtList);

        // Assert
        String expectedQuery = " SELECT ... FROM ...  ORDER BY btr.createdtime DESC";
        assertEquals(expectedQuery, query);
    }

}
