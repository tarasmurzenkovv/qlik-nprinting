I. PREREQUISITES

    1. The current user must registered in QLIK/Nprinting systems
    2. The current application must have all rights to read and write to the specified folder

II. PROGRAM USAGE

    1. cd in the folder, where jar with application is located
    2. create the application.properties file
    3. execute the following
        > java -jar nprinting_connector.jar -file=pathToPropertiesFile -filters="filtersAsString"
    4. after that the test report will be downloaded to the selected folder

III. HOW TO CONFIGURE APPLICATION?

    The application configuration is done via passing to the program arguments the path to the application properties file.
    All fields in this file are mandatory and must be filled with the proper values. The sample structure of the properties
    file is below

    login=the current user login without domain
    password=the current user password
    port=host port (e.x. port value)
    host=nprinting host (e.x. host name)
    path= the path folder where reports will be downloaded and stored (e.x. C:\\reports for Windows systems)


IV. HOW TO SPECIFY FILTERS?

    Filter section consists of filter groups, separated by '#' e.x. group_1#group_2#group_3
    Each filter group contains the filter values, separated by ',' e.x. fieldName=FIRSTNAME, selectedCount=3, selectedValues=THOMAS&CHRISTOPHER&SUMMER, isNumeric=false
    Each filter value is the name-value pair separated by '=' e.x. fieldName=FIRSTNAME or selectedValues=THOMAS&CHRISTOPHER&SUMMER
    Value can have either one value (e.x. FIRSTNAME) or numeriouse values (e.x.THOMAS&CHRISTOPHER&SUMMER) separated by '&'
    Available names are the following - fieldName, selectedCount, selectedValues, isNumeric

    Full example

    fieldName=FIRSTNAME, selectedCount=3, selectedValues=THOMAS&CHRISTOPHER&SUMMER, isNumeric=false#
    fieldName=LASTNAME, selectedCount=2, selectedValues=SMITH&PADDLE, isNumeric=false#
    fieldName=FULLNAME, selectedCount=3, selectedValues=THOMAS PADDLE&UMMER PADDLE&CHRISTOPHER SMITH, isNumeric=false#
    fieldName=BANKNAME, selectedCount=4, selectedValues=Cronos Bank&Zeus Bank&LOL Bank&Jupiter Bank, isNumeric=false

V. HOW TO BUILD FORM SOURCES

    1. execute gradlew build
    2. the jar will placed at the following folder ./nprinting_connector/build/lubs/nprinting_connector.jar

VI. Additional links

    1. REST API documentation -- http://help.qlik.com/en-US/nprinting/September2017/APIs/NP%20API/#ondemandRequestsPost