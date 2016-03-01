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
import com.tableausoftware.server.*;

public final class PublishExtract {

    public static void main( String[] args ) {

        try {
            // Initialize Tableau Server API
            ServerAPI.initialize();

            // Create the server connection object
            ServerConnection serverConnection = new ServerConnection();

            // Connect to the server
            serverConnection.connect("http://localhost", "username", "password", "siteID");

            // Publish order-java.tde to the server under the default project with name Order-java
            serverConnection.publishExtract("order-java.tde", "default", "Order-java", false);

            // Disconnect from the server
            serverConnection.disconnect();

            // Destroy the server connection object
            serverConnection.close();

            // Clean up Tableau Server API
            ServerAPI.cleanup();
        }
        catch (TableauException e) {
            // Handle the exception depending on the type of exception received

            switch(Result.enumForValue(e.getErrorCode())) {
            case INTERNAL_ERROR:
                System.err.println("INTERNAL_ERROR - Could not parse the response from the server.");
                break;
            case INVALID_ARGUMENT:
                System.err.println("INVALID_ARGUMENT - " + e.getMessage());
                break;
            case CURL_ERROR:
                System.err.println("CURL_ERROR - " + e.getMessage());
                break;
            case SERVER_ERROR:
                System.err.println("SERVER_ERROR - " + e.getMessage());
                break;
            case NOT_AUTHENTICATED:
                System.err.println("NOT_AUTHENTICATED - " + e.getMessage());
                break;
            case BAD_PAYLOAD:
                System.err.println("BAD_PAYLOAD - Unknown response from the server. Make sure this version of Tableau API is compatible with your server.");
                break;
            case INIT_ERROR:
                System.err.println("INIT_ERROR - " + e.getMessage());
                break;
            case UNKNOWN_ERROR:
            default:
                System.err.println("An unknown error occured.");
                break;
            }
            e.printStackTrace(System.err);
        }
    }
}
