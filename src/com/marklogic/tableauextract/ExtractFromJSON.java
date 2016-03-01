/**
 * -----------------------------------------------------------------------------
 * 
 * This file is the copyrighted property of Tableau Software and is protected 
 * by registered patents and other applicable U.S. and international laws and 
 * regulations.
 * 
 * Unlicensed use of the contents of this file is prohibited. Please refer to 
 * the NOTICES.txt file for further details.
 * 
 * -----------------------------------------------------------------------------
 */
package com.marklogic.tableauextract;

import com.tableausoftware.TableauException;
import com.tableausoftware.common.*;
import com.tableausoftware.extract.*;

import org.json.simple.JSONArray;  
import org.json.simple.JSONObject;  
import org.json.simple.parser.JSONParser;  
import org.json.simple.parser.ParseException;

import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.time.LocalDate;


public final class ExtractFromJSON {

    // Define the table's schema
    private static TableDefinition makeTableDefinition() throws TableauException {
        TableDefinition tableDef = new TableDefinition();
        tableDef.setDefaultCollation(Collation.EN_GB);

        tableDef.addColumn("id",        Type.INTEGER);
        tableDef.addColumn("ssn",         Type.CHAR_STRING);
        tableDef.addColumn("type",         Type.CHAR_STRING);
        tableDef.addColumn("payment_amount",           Type.DOUBLE);
        tableDef.addColumn("claim_date", Type.DATE);

        // Column with non-default collation
        // tableDef.addColumnWithCollation("id", Type.CHAR_STRING, Collation.DE);

        return tableDef;
    }

    // Print a Table's schema to stderr.
    private static void printTableDefinition(TableDefinition tableDef) throws TableauException {
        int numColumns = tableDef.getColumnCount();
        for ( int i = 0; i < numColumns; ++i ) {
            Type type = tableDef.getColumnType(i);
            String name = tableDef.getColumnName(i);

            System.err.format("Column %d: %s (%#06x)\n", i, name, type.getValue());
        }
    }

    /**
     * Read JSON output from MarkLogic REST extension or *.xqy file and insert
     * the output into a Tabeleau table
     * 
     * @param table
     * @throws TableauException
     */
    private static void insertData(Table table) throws TableauException, FileNotFoundException, ParseException, IOException {
        TableDefinition tableDef = table.getTableDefinition();
        Row row = new Row(tableDef);
        
        URL url = new URL("http://localhost:8060/json.xqy");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(bufferedReader);

        JSONArray claims = (JSONArray) jsonObject.get("claims");
        
        @SuppressWarnings("unchecked")
		Iterator<JSONObject> i = claims.iterator();
        
        while (i.hasNext()) {
        	JSONObject innerObject = i.next();
        	
        	String idString = (String)innerObject.get("id");
            row.setInteger(0, Integer.parseInt(idString.substring(0,6)));
        	row.setCharString(1, (String)innerObject.get("ssn"));
        	row.setCharString(2, (String)innerObject.get("type"));
        	String payString = (String)innerObject.get("payment_amount");
        	if (payString == null || payString.isEmpty()) payString = "0.0";
        	row.setDouble(3, Double.parseDouble(payString));
        	String dtString = (String)innerObject.get("claim_date");
        	if (dtString == null || dtString.isEmpty()) dtString = "1999-01-01";
       	    LocalDate claimDate = (LocalDate.parse(dtString));
        	row.setDate(4, claimDate.getYear(), claimDate.getMonthValue(), claimDate.getDayOfMonth());
            table.insert(row);
        	
        }
        
        /*
        row.setDateTime(  0, 2012, 7, 3, 11, 40, 12, 4550); // Purchased
        row.setCharString(1, "Beans");                      // Product
        row.setString(    2, "uniBeans");                   // uProduct
        row.setDouble(    3, 1.08);                         // Price
        row.setDate(      6, 2029, 1, 1);                   // Expiration date
        row.setCharString(7, "Bohnen");                     // Produkt

        for ( int i = 0; i < 10; ++i ) {
            row.setInteger(4, i * 10);                      // Quantity
            row.setBoolean(5, i % 2 == 1);                  // Taxed
        }
        */
    }

    public static void main( String[] args ) {

        try {
            // Initialize Tableau Extract API
            ExtractAPI.initialize();

            try (Extract extract = new Extract("claim-java.tde")) {

                Table table;
                if (!extract.hasTable("Extract")) {
                    // Table does not exist; create it
                    TableDefinition tableDef = makeTableDefinition();        
                    table = extract.addTable("Extract", tableDef);
                }
                else {
                    // Open an existing table to add more rows
                    table = extract.openTable("Extract");
                }

                TableDefinition tableDef = table.getTableDefinition();
                printTableDefinition(tableDef);

                insertData(table);
            }

            // Clean up Tableau Extract API
            ExtractAPI.cleanup();
        }
        catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }
}
