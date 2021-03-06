
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author asakpere
 */
//import static LDiversity.lValue;
public class TCloseness {

    static String sensitive_attribute = "`CRIME_TYPE`";
    static String quasi_attribute = "`RESIDENCE`";
    static double tValue = 0.05;
    public static int satisfiedTCloseness = 0;
    public static int notSatisfiedTCloseness = 0;
    
    

    public static String[][] t_closeness(int kAnon, float tvalue) {
        for (int i = 0; i < 9; i++) {

        }
        return null;
    }

    public static void setTclosenessEquivalenceClass() {
        // get a DBManager
        DBManager dbManager = new DBManager();

        // create sql to get all Ids
        String sql = "SELECT " + sensitive_attribute + " " + quasi_attribute
                + "FROM Crime";
        String[] query_result = dbManager.runQuery(sql);
        System.out.println("tcloseness query_result  " + query_result.length);
        prepareForTCloseness(dbManager, query_result.length);
    }
    static int countOfRecNotGoingTCloseness = 0;

    // select crime_type, anonymized_residence as anon_res, count(anonymized_residence), count(anonymized_residence)/(select count(anonymized_residence) from Phase3.EquivalenceClassTable where anonymized_residence = anon_res group by anonymized_residence) as EqClass_saDist from Phase3.EquivalenceClassTable group by crime_type, anonymized_residence;
    // SELECT DISTINCT CRIME_TYPE, Anonymized_residence, COUNT(CRIME_TYPE), COUNT(CRIME_TYPE)/(SELECT COUNT(CRIME_TYPE) FROM Phase3.EquivalenceClassTable) as sens_att_dist FROM Phase3.EquivalenceClassTable GROUP BY CRIME_TYPE, anonymized_residence;
    private static void prepareForTCloseness(DBManager dBManager, int noOfRecordsInBuffer) {

        String[] result = null;
        String[] result2 = null;
        String[] resultForSpecific = null;
        String[] resultForSpecific2 = null;
        String[] resultForSpecific3 = null;
        // get a DBManager
        DBManager dbManager = new DBManager();

        try {

            String sql = "SELECT ANONYMIZED_RESIDENCE FROM EquivalenceClassTable";
            // String anon_res;
            //String sqlEqvClass = "SELECT anonymized_residence FROM EquivalenceClassTable where anonymized_residece = " + anon_res;
            String[] query_result = dbManager.runQuery(sql);
            //String sql_query = "SELECT DISTINCT CRIME_TYPE, COUNT(DISTINCT CRIME_TYPE), COUNT(DISTINCT CRIME_TYPE)/" + query_result.length + "FROM EquivalenceClassTable GROUP BY CRIME_TYPE";
            String sql_query = "SELECT DISTINCT CRIME_TYPE FROM EquivalenceClassTable GROUP BY CRIME_TYPE";

            String queryForSpecific = "SELECT COUNT(ANONYMIZED_RESIDENCE) FROM EquivalenceClassTable GROUP BY ANONYMIZED_RESIDENCE";
            String queryForSpecific2 = "SELECT ANONYMIZED_RESIDENCE FROM EquivalenceClassTable GROUP BY ANONYMIZED_RESIDENCE";

            // , COUNT(DISTINCT CRIME_TYPE)/(SELECT COUNT(CRIME_TYPE) FROM EquivalenceClassTable)
            //String sql_query2 = "SELECT DISTINCT(COUNT(CRIME_TYPE)), ANONYMIZED_RESIDENCE FROM EquivalenceClassTable GROUP BY ANONYMIZED_RESIDENCE";
            result = dBManager.runQuery(sql_query);
            resultForSpecific = dBManager.runQuery(queryForSpecific);
            resultForSpecific2 = dBManager.runQuery(queryForSpecific2);
            resultForSpecific3 = dBManager.runQuery(sql);

        } catch (Exception e) {

            e.printStackTrace();
        }
        String[][] sensAttrForBuffer = new String[result.length][3];//3 becos we have crime_type,frequency,saDistValue in the columns
        for (int i = 0; i < result.length; i++) {
            //System.err.println("Result is "+ result[i]);
            String sql_query2 = "SELECT CRIME_TYPE FROM EquivalenceClassTable WHERE CRIME_TYPE = '" + result[i] + "'";
            result2 = dBManager.runQuery(sql_query2);
            //System.err.println("No of  "+result[i] +" is "+ result2.length);
            sensAttrForBuffer[i][0] = result[i];
            sensAttrForBuffer[i][1] = String.valueOf(result2.length);
            sensAttrForBuffer[i][2] = String.valueOf((double) result2.length / noOfRecordsInBuffer);
        }

        //ArrayList<String[][]> realResult = new ArrayList<>();
        String[][] sesAttrDistForEqClass = new String[resultForSpecific3.length][5];
        int y = 0;
        for (int i = 0; i < resultForSpecific2.length; i++) {
            String sql_query = "SELECT DISTINCT CRIME_TYPE  FROM EquivalenceClassTable WHERE ANONYMIZED_RESIDENCE ='" + resultForSpecific2[i] + "' GROUP BY CRIME_TYPE";
            //String query4 = "SELECT CRIME_TYPE from EquivalenceClassTable WHERE ANONYMIZED_RESIDENCE ='"+resultForSpecific2[i]+"'";
            String result_1[] = dBManager.runQuery(sql_query);
            //String result_2[] = dBManager.runQuery(query4);
            System.out.println("The number of records in " + resultForSpecific2[i] + " is " + resultForSpecific[i]);

            System.out.print("The distinct crimes, in eq class " + resultForSpecific2[i] + " are");
            for (int j = 0; j < result_1.length; j++) {

                if (Integer.parseInt(resultForSpecific[i]) < Phase3GUI.kAnon) {
                    System.out.println("Anonymised res " + resultForSpecific2[i] + " cannot go for T Closeness.");
                    countOfRecNotGoingTCloseness += Integer.parseInt(resultForSpecific[i]);
                } else {
                    String sql_query2 = "SELECT CRIME_TYPE FROM EquivalenceClassTable WHERE CRIME_TYPE = '" + result_1[j]
                            + "' AND ANONYMIZED_RESIDENCE ='" + resultForSpecific2[i] + "'";
                    String result3[] = dBManager.runQuery(sql_query2);
                    int z = 0;
                    sesAttrDistForEqClass[y][z] = result_1[j]; //the crime type
                    sesAttrDistForEqClass[y][++z] = String.valueOf(result3.length); //the freq of the crime type
                    sesAttrDistForEqClass[y][++z] = resultForSpecific2[i];// the anonymized res
                    sesAttrDistForEqClass[y][++z] = resultForSpecific[i]; //the total no of records in the anonymized res.
                    sesAttrDistForEqClass[y][++z] = String.valueOf((double) result3.length / Integer.parseInt(resultForSpecific[i]));
                    y++;
                    System.out.print(result_1[j] + " with length " + result3.length);
                }
            }
            System.out.println("");

        }
        System.out.println("Dist based on buffer");
        for (int i = 0; i < sensAttrForBuffer.length; i++) {
            System.out.println("Distr Value is " + sensAttrForBuffer[i][0] + "," + sensAttrForBuffer[i][1] + "," + sensAttrForBuffer[i][2]);
        }
        System.out.println("Dist based on equivalence classes");
        for (int i = 0; i < sesAttrDistForEqClass.length; i++) {
            if (sesAttrDistForEqClass[i][0] != null) {
                System.out.println("Distr Value is " + sesAttrDistForEqClass[i][0] + "," + sesAttrDistForEqClass[i][1] + "," + sesAttrDistForEqClass[i][2] + "," + sesAttrDistForEqClass[i][3] + "," + sesAttrDistForEqClass[i][4]);
            }
        }

        confirmTCloseness(sensAttrForBuffer,sesAttrDistForEqClass, tValue);
//        String[][] bufferSenDis = new String[result.length / 3][3];
//
//        int value = -1;
//        for (int i = 0; i < bufferSenDis.length; i++) {
//            System.out.print("Eventual result is ");
//            for (int j = 0; j < bufferSenDis[i].length; j++) {
//                value++;
//                bufferSenDis[i][j] = result[value];
//                System.out.print(bufferSenDis[i][j] + ", ");
//            }
//            System.out.println();
//        }

        /*  for (int i = 0; i < size; i++) {
                waiting_time = dbMngr.runQuery(String.format("SELECT ExpectedWaitingTime FROM Phase3.Crime WHERE RESIDENCE = '" + splitsuppressedRecArray[i] + "'"));
                System.out.println("The length is "+ waiting_time.length + " i.e "+ waiting_time.length +" instances of "+ splitsuppressedRecArray[i]+" found.");
                for(String time:waiting_time){
                 System.out.println("The waiting time for suppressed record " + splitsuppressedRecArray[i] + " is " +time);
               }
         }*/
        // confirmLDiversity(forTCloseness,lValue,Phase3GUI.kAnon);
    }

    private static void confirmTCloseness(String[][] sensAttrDistForBuffer,String[][] sensAttrDistForEqClass, double t) {

        for (int j = 0; j < sensAttrDistForEqClass.length; j++) {
            if(sensAttrDistForEqClass[j][0] != null){
            String eqClassCrimeType = sensAttrDistForEqClass[j][0];
            double eqClassProb = Double.parseDouble(sensAttrDistForEqClass[j][4]);
            for(int m=0;m<sensAttrDistForBuffer.length;m++){
            
                String bufferCrimeType = sensAttrDistForBuffer[m][0];
                double bufferProb = Double.parseDouble(sensAttrDistForBuffer[m][2]);
                if(eqClassCrimeType.equals(bufferCrimeType)){
                    double absoluteProb = Math.abs(bufferProb-eqClassProb);
                    if(absoluteProb <tValue){
                        System.out.println("T closesness sat for "+eqClassCrimeType);
                        satisfiedTCloseness +=1;
                    }else{
                         System.out.println("T closesness not sat for "+eqClassCrimeType);
                        notSatisfiedTCloseness +=1;
                    }
                }
            
            }
        }
        }

    }

}
