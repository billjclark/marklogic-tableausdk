# marklogic-samplestack

This sample application demonstrates how to put MarkLogic data into a Tableau Data Extract via a rest extension or *.xqy file.  The rest extension or *.xqy file can actually output data in any format as long as the Tableau SDK program can read the format.  Valid formats include JSPN, XML, and CSV.

We advise you to keep your output from MarkLogc flat because ultimately, you need to generate table that Tableau understands.

We also recommend that you output JSON because this is the most used format at Tableau.

If you are going to load a lot of documents into a Tableau Data Extract, we recommend that you use pagination.  If there are a lot of updates in the MarkLogic database during the creation of the tableau data extract, we recommend implementing a point-in-time query.

#Project files are:

	-com/marklogic/tableauextract/ExtractFromJSON.java - Main Java class that generates tde file
	-com/marklogic/tableauextract/PublishExtract.java - Simple program to copy tde file to Tableau server
	-claims.xml - 100 simple claim documents to load into a MarkLogc database
	-json.xqy - Simple JQuery that generates JSON from MarkLogic documents


#The steps to using the Tableau SDK for Java in this example are:

1) Download and install the Tableau SDK from https://onlinehelp.tableau.com/current/api/sdk/en-us/help.htm#SDK/tableau_sdk_installing.htm%3FTocPath%3D_____3
2) Add the SDK Bin directory to the PATH variable.  You need this so Java can find the DLL's that are needed to generate the tde files.  For example:  

path=...;C:\Program Files\Tableau\SDK\bin

3) Get it to work with Eclipse IDE (https://onlinehelp.tableau.com/current/api/sdk/en-us/help.htm#SDK/tableau_sdk_using_java_eclipse.htm%3FTocPath%3D_____5)
4) If you are going to use Simple JSON, download a recent version such as "json-simple-1.1.1.jar" and place in the plugins directory, such as c:/eclipse/plugins
5) Add the simple json jar to the project
6) Use MLCP to load the 100 claim records found in claims.xml
7) Create a file based http server in MarkLogic and place json.xqy in the server's home directory.
8) Test to make sure that valid JSON is being returned by entering something like "http://localhost:8060/json.xqy" into a browser.
9) Edit the ExtractFromJSON.java - Enter the JSON URL and the tde file name.
10) Execute ExtractFromJSON.class and look for the tde file.

# MarkLogic Tableau SDK for C/C++ or Python Examples

TBD.  The concept is the same as the Java implementation.
