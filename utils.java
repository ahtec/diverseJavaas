/*
 * utils.java
 *
 * Created on 18 januari 2007, 15:57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package wm;

import com.wm.app.b2b.client.ServiceException;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author tzxj7b
 */
public class utils {
    
    public static final void    SyntaxCheck_BSI_NETTING ( IData pipeline ) throws ServiceException {
        
        
        IDataCursor pipelineCursor = pipeline.getCursor ();
        String	in = IDataUtil.getString ( pipelineCursor, "in" );
        pipelineCursor.destroy ();
        String message;
        String[] infields ;
        String[] inRows = in.split ("\n");
        for ( int i = 0; i < inRows.length; i++ ) {
            message = "";
            infields = inRows[i].split ("\t");
            // ************************   check for 7 fields in row   *******************
            if (infields.length != 7){
                message = "Error:SyntaxCheck_BSI_NETTING: # fields is "+infields.length+". It should be equal to 7, row "+i+" is not valid, file rejected";
                try {
//                    Values vv = new Values ();
//                    vv.put ("message", message);
//                    vv.put ("function", "LOGOPP");
//                    vv.put ("level", 4);
//                    Service.doInvoke ("pub.flow", "debugLog", vv);
                } catch (Exception elog) { throw new ServiceException (message );}
                break;
            }
            // ************************   check modelId   *******************
            if (infields[0].length () > 22 ){
                message = "Error:SyntaxCheck_BSI_NETTING: Model Id  "+infields[0]+" is invalid, to long. Max is 22 positions, row "+i+" is not valid, file rejected";
                try {
//                    Values vv = new Values ();
//                    vv.put ("message", message);
//                    vv.put ("function", "LOGOPP");
//                    vv.put ("level", 4);
//                    Service.doInvoke ("pub.flow", "debugLog", vv);
                } catch (Exception elog) { throw new ServiceException (message );}
                break;
            }
            
            // ************************   check amount   *******************
            {
                String validChars = "0123456789.,";
                boolean  IsNumber=true;
                String s;
                String field = infields[4];
                for (int k = 0; k < field.length () && IsNumber == true; k++) {
                    s  = field.substring (k,k+1);
                    if (validChars.indexOf (s) == -1) {
                        IsNumber = false;
                        message = "Error:SyntaxCheck_BSI_NETTING:: amount   "+infields[4]+" is not numeric,  row "+i+" is not valid, file rejected";
                        try {
//                            Values vv = new Values ();
//                            vv.put ("message", message);
//                            vv.put ("function", "LOGOPP");
//                            vv.put ("level", 4);
//                            Service.doInvoke ("pub.flow", "debugLog", vv);
                        } catch (Exception elog) { throw new ServiceException (message );}
                        break;
                    }
                }
                
            }
        }
        message = "Error:SyntaxCheck_BSI_NETTING: ";
        try {
//            Values vv = new Values ();
//            vv.put ("message", message);
//            vv.put ("function", "LOGOPP");
//            vv.put ("level", 4);
//            Service.doInvoke ("pub.flow", "debugLog", vv);
        } catch (Exception elog) { throw new ServiceException (message );}
        
        
        
    }
    
    
    
    
    /** Creates a new instance of utils */
    public utils () {
    }
    
    public static final void addPartyToPaymentIfTypeNotInPartyJava ( IData pipeline ) throws ServiceException {
        
// pipeline
        IDataCursor pipelineCursor = pipeline.getCursor ();
        IDataCursor OPPPaymentsCursor;
// check if parameter OPPPayments has a party (/PARTY)       !%OPPPayments/PARTY%
        
        // OPPPayments
        IData	OPPPayments = IDataUtil.getIData ( pipelineCursor, "OPPPayments" );
        if ( OPPPayments != null) {
            OPPPaymentsCursor = OPPPayments.getCursor ();
            // i.PARTY
            IData[]	PARTY = IDataUtil.getIDataArray ( OPPPaymentsCursor, "PARTY" );
            if ( PARTY != null) {
                // if   OPPPayments has not got a   party then we have to ADD one
                IData[]	PARTY_1 = new IData[1];
                PARTY_1[0] = IDataFactory.create ();
                IDataUtil.put ( OPPPaymentsCursor, "PARTY", PARTY_1 );
            }
            
            // if param partytype string eq to param the property  TYPE from OPPPaymentsParty ot TYPE is nill then
            IData OPPPaymentsParty = IDataUtil.getIData ( pipelineCursor, "OPPPaymentsParty" );
            if ( OPPPaymentsParty != null) {
                String PartyType = IDataUtil.getString ( pipelineCursor, "PartyType" );
                IDataCursor OPPPaymentsPartyCursor = OPPPaymentsParty.getCursor ();
                String OPPPaymentsParty_type = IDataUtil.getString ( OPPPaymentsPartyCursor, "TYPE" );
                if ((OPPPaymentsParty_type == null )  || ( ! OPPPaymentsParty_type.equalsIgnoreCase (PartyType))) {
                    // vul property  TYPE from OPPPaymentsParty met param string PartyType
                    IDataUtil.put ( OPPPaymentsPartyCursor, "TYPE", PartyType );
                    // append OPPPaymentsParty to OPPPayments
                    IDataUtil.put ( OPPPaymentsCursor, "PARTY", OPPPaymentsParty );
                    if (PartyType.equalsIgnoreCase ("DB")) {
                        // get the cacheParty
                        //fill it with the param party
                        // CacheParty
                        IData	CacheParty = IDataUtil.getIData ( pipelineCursor, "CacheParty" );
                        if ( CacheParty != null) {
                            IDataCursor CachePartyCursor = CacheParty.getCursor ();
                            IData	DEBIT_BANK = IDataUtil.getIData ( CachePartyCursor, "DEBIT_BANK" );
                            if ( DEBIT_BANK != null) {
                                IDataCursor DEBIT_BANKCursor = DEBIT_BANK.getCursor ();
                            } else {
                                IData	DEBIT_BANK_1 = IDataFactory.create ();
                                IDataCursor DEBIT_BANK_1Cursor = DEBIT_BANK_1.getCursor ();
                                
                                
                            }
                        }
                    }
                }
            }
        }
    }
/*                            for ( int i = 0; i < PARTY.length; i++ )
                                {
                                        IDataCursor PARTYCursor = PARTY[i].getCursor();
 
                                                // i_1.cache
                                                IData	cache = IDataUtil.getIData( PARTYCursor, "cache" );
                                                if ( cache != null)
                                                {
                                                        IDataCursor cacheCursor = cache.getCursor();
                                                        cacheCursor.destroy();
                                                }
                                        PARTYCursor.destroy();
                                }
                        } else {
 
                        }
 
                        // i_1.cache
                        IData	cache_1 = IDataUtil.getIData( OPPPaymentsCursor, "cache" );
                        if ( cache_1 != null)
                        {
                                IDataCursor cache_1Cursor = cache_1.getCursor();
                                cache_1Cursor.destroy();
                        }
                OPPPaymentsCursor.destroy();
 
 
        // OPPPaymentsParty
        IData	OPPPaymentsParty = IDataUtil.getIData( pipelineCursor, "OPPPaymentsParty" );
        if ( OPPPaymentsParty != null)
        {
                IDataCursor OPPPaymentsPartyCursor = OPPPaymentsParty.getCursor();
 
                        // i_1.cache
                        IData	cache_2 = IDataUtil.getIData( OPPPaymentsPartyCursor, "cache" );
                        if ( cache_2 != null)
                        {
                                IDataCursor cache_2Cursor = cache_2.getCursor();
                                cache_2Cursor.destroy();
                        }
                OPPPaymentsPartyCursor.destroy();
        }
        String	PartyType = IDataUtil.getString( pipelineCursor, "PartyType" );
 
        // CacheParty
        IData	CacheParty = IDataUtil.getIData( pipelineCursor, "CacheParty" );
        if ( CacheParty != null)
        {
                IDataCursor CachePartyCursor = CacheParty.getCursor();
 
                        // i_1.BENEFICIARY
                        IData	BENEFICIARY = IDataUtil.getIData( CachePartyCursor, "BENEFICIARY" );
                        if ( BENEFICIARY != null)
                        {
                                IDataCursor BENEFICIARYCursor = BENEFICIARY.getCursor();
 
                                        // i_1.cache
                                        IData	cache_3 = IDataUtil.getIData( BENEFICIARYCursor, "cache" );
                                        if ( cache_3 != null)
                                        {
                                                IDataCursor cache_3Cursor = cache_3.getCursor();
                                                cache_3Cursor.destroy();
                                        }
                                BENEFICIARYCursor.destroy();
                        }
 
                        // i_1.BENEFICIARY_INSTITUTION
                        IData	BENEFICIARY_INSTITUTION = IDataUtil.getIData( CachePartyCursor, "BENEFICIARY_INSTITUTION" );
                        if ( BENEFICIARY_INSTITUTION != null)
                        {
                                IDataCursor BENEFICIARY_INSTITUTIONCursor = BENEFICIARY_INSTITUTION.getCursor();
 
                                        // i_1.cache
                                        IData	cache_4 = IDataUtil.getIData( BENEFICIARY_INSTITUTIONCursor, "cache" );
                                        if ( cache_4 != null)
                                        {
                                                IDataCursor cache_4Cursor = cache_4.getCursor();
                                                cache_4Cursor.destroy();
                                        }
                                BENEFICIARY_INSTITUTIONCursor.destroy();
                        }
 
                        // i_1.CHEQUE_RECEIVER
                        IData	CHEQUE_RECEIVER = IDataUtil.getIData( CachePartyCursor, "CHEQUE_RECEIVER" );
                        if ( CHEQUE_RECEIVER != null)
                        {
                                IDataCursor CHEQUE_RECEIVERCursor = CHEQUE_RECEIVER.getCursor();
 
 
                                        // i_1.cache
                                        IData	cache_5 = IDataUtil.getIData( CHEQUE_RECEIVERCursor, "cache" );
                                        if ( cache_5 != null)
                                        {
                                                IDataCursor cache_5Cursor = cache_5.getCursor();
 
                                                cache_5Cursor.destroy();
                                        }
                                CHEQUE_RECEIVERCursor.destroy();
                        }
 
                        // i_1.ACCOUNT_WITH_INSTITUTION
                        IData	ACCOUNT_WITH_INSTITUTION = IDataUtil.getIData( CachePartyCursor, "ACCOUNT_WITH_INSTITUTION" );
                        if ( ACCOUNT_WITH_INSTITUTION != null)
                        {
                                IDataCursor ACCOUNT_WITH_INSTITUTIONCursor = ACCOUNT_WITH_INSTITUTION.getCursor();
 
                                        // i_1.cache
                                        IData	cache_6 = IDataUtil.getIData( ACCOUNT_WITH_INSTITUTIONCursor, "cache" );
                                        if ( cache_6 != null)
                                        {
                                                IDataCursor cache_6Cursor = cache_6.getCursor();
                                                cache_6Cursor.destroy();
                                        }
                                ACCOUNT_WITH_INSTITUTIONCursor.destroy();
                        }
 
                        // i_1.DEBIT_BANK
                        IData	DEBIT_BANK = IDataUtil.getIData( CachePartyCursor, "DEBIT_BANK" );
                        if ( DEBIT_BANK != null)
                        {
                                IDataCursor DEBIT_BANKCursor = DEBIT_BANK.getCursor();
 
                                        // i_1.cache
                                        IData	cache_7 = IDataUtil.getIData( DEBIT_BANKCursor, "cache" );
                                        if ( cache_7 != null)
                                        {
                                                IDataCursor cache_7Cursor = cache_7.getCursor();
                                                cache_7Cursor.destroy();
                                        }
                                DEBIT_BANKCursor.destroy();
                        }
 
                        // i_1.DEBIT_CUSTOMER
                        IData	DEBIT_CUSTOMER = IDataUtil.getIData( CachePartyCursor, "DEBIT_CUSTOMER" );
                        if ( DEBIT_CUSTOMER != null)
                        {
                                IDataCursor DEBIT_CUSTOMERCursor = DEBIT_CUSTOMER.getCursor();
 
                                        // i_1.cache
                                        IData	cache_8 = IDataUtil.getIData( DEBIT_CUSTOMERCursor, "cache" );
                                        if ( cache_8 != null)
                                        {
                                                IDataCursor cache_8Cursor = cache_8.getCursor();
                                                cache_8Cursor.destroy();
                                        }
                                DEBIT_CUSTOMERCursor.destroy();
                        }
 
                        // i_1.INTERMEDIARY_INSTITUTION
                        IData	INTERMEDIARY_INSTITUTION = IDataUtil.getIData( CachePartyCursor, "INTERMEDIARY_INSTITUTION" );
                        if ( INTERMEDIARY_INSTITUTION != null)
                        {
                                IDataCursor INTERMEDIARY_INSTITUTIONCursor = INTERMEDIARY_INSTITUTION.getCursor();
 
                                        // i_1.cache
                                        IData	cache_9 = IDataUtil.getIData( INTERMEDIARY_INSTITUTIONCursor, "cache" );
                                        if ( cache_9 != null)
                                        {
                                                IDataCursor cache_9Cursor = cache_9.getCursor();
                                                cache_9Cursor.destroy();
                                        }
                                INTERMEDIARY_INSTITUTIONCursor.destroy();
                        }
 
                        // i_1.ORDERING_CUSTOMER
                        IData	ORDERING_CUSTOMER = IDataUtil.getIData( CachePartyCursor, "ORDERING_CUSTOMER" );
                        if ( ORDERING_CUSTOMER != null)
                        {
                                IDataCursor ORDERING_CUSTOMERCursor = ORDERING_CUSTOMER.getCursor();
 
                                        // i_1.cache
                                        IData	cache_10 = IDataUtil.getIData( ORDERING_CUSTOMERCursor, "cache" );
                                        if ( cache_10 != null)
                                        {
                                                IDataCursor cache_10Cursor = cache_10.getCursor();
                                                cache_10Cursor.destroy();
                                        }
                                ORDERING_CUSTOMERCursor.destroy();
                        }
 
                        // i_1.ORDERING_INSTITUTION
                        IData	ORDERING_INSTITUTION = IDataUtil.getIData( CachePartyCursor, "ORDERING_INSTITUTION" );
                        if ( ORDERING_INSTITUTION != null)
                        {
                                IDataCursor ORDERING_INSTITUTIONCursor = ORDERING_INSTITUTION.getCursor();
 
                                        // i_1.cache
                                        IData	cache_11 = IDataUtil.getIData( ORDERING_INSTITUTIONCursor, "cache" );
                                        if ( cache_11 != null)
                                        {
                                                IDataCursor cache_11Cursor = cache_11.getCursor();
                                                cache_11Cursor.destroy();
                                        }
                                ORDERING_INSTITUTIONCursor.destroy();
                        }
 
                        // i_1.RECEIVERS_CORRESPONDENT
                        IData	RECEIVERS_CORRESPONDENT = IDataUtil.getIData( CachePartyCursor, "RECEIVERS_CORRESPONDENT" );
                        if ( RECEIVERS_CORRESPONDENT != null)
                        {
                                IDataCursor RECEIVERS_CORRESPONDENTCursor = RECEIVERS_CORRESPONDENT.getCursor();
 
                                        // i_1.cache
                                        IData	cache_12 = IDataUtil.getIData( RECEIVERS_CORRESPONDENTCursor, "cache" );
                                        if ( cache_12 != null)
                                        {
                                                IDataCursor cache_12Cursor = cache_12.getCursor();
                                                cache_12Cursor.destroy();
                                        }
                                RECEIVERS_CORRESPONDENTCursor.destroy();
                        }
 
                        // i_1.SENDERS_CORRESPONDENT
                        IData	SENDERS_CORRESPONDENT = IDataUtil.getIData( CachePartyCursor, "SENDERS_CORRESPONDENT" );
                        if ( SENDERS_CORRESPONDENT != null)
                        {
                                IDataCursor SENDERS_CORRESPONDENTCursor = SENDERS_CORRESPONDENT.getCursor();
 
                                        // i_1.cache
                                        IData	cache_13 = IDataUtil.getIData( SENDERS_CORRESPONDENTCursor, "cache" );
                                        if ( cache_13 != null)
                                        {
                                                IDataCursor cache_13Cursor = cache_13.getCursor();
                                                cache_13Cursor.destroy();
                                        }
                                SENDERS_CORRESPONDENTCursor.destroy();
                        }
 
                        // i_1.SENDING_INSTITUTION
                        IData	SENDING_INSTITUTION = IDataUtil.getIData( CachePartyCursor, "SENDING_INSTITUTION" );
                        if ( SENDING_INSTITUTION != null)
                        {
                                IDataCursor SENDING_INSTITUTIONCursor = SENDING_INSTITUTION.getCursor();
 
                                        // i_1.cache
                                        IData	cache_14 = IDataUtil.getIData( SENDING_INSTITUTIONCursor, "cache" );
                                        if ( cache_14 != null)
                                        {
                                                IDataCursor cache_14Cursor = cache_14.getCursor();
                                                cache_14Cursor.destroy();
                                        }
                                SENDING_INSTITUTIONCursor.destroy();
                        }
 
                        // i_1.THIRD_REIMBURSEMENT_INST
                        IData	THIRD_REIMBURSEMENT_INST = IDataUtil.getIData( CachePartyCursor, "THIRD_REIMBURSEMENT_INST" );
                        if ( THIRD_REIMBURSEMENT_INST != null)
                        {
                                IDataCursor THIRD_REIMBURSEMENT_INSTCursor = THIRD_REIMBURSEMENT_INST.getCursor();
 
                                        // i_1.cache
                                        IData	cache_15 = IDataUtil.getIData( THIRD_REIMBURSEMENT_INSTCursor, "cache" );
                                        if ( cache_15 != null)
                                        {
                                                IDataCursor cache_15Cursor = cache_15.getCursor();
                                                cache_15Cursor.destroy();
                                        }
                                THIRD_REIMBURSEMENT_INSTCursor.destroy();
                        }
 
                        // i_1.CHEQUE_DEPOSIT
                        IData	CHEQUE_DEPOSIT = IDataUtil.getIData( CachePartyCursor, "CHEQUE_DEPOSIT" );
                        if ( CHEQUE_DEPOSIT != null)
                        {
                                IDataCursor CHEQUE_DEPOSITCursor = CHEQUE_DEPOSIT.getCursor();
 
                                        // i_1.cache
                                        IData	cache_16 = IDataUtil.getIData( CHEQUE_DEPOSITCursor, "cache" );
                                        if ( cache_16 != null)
                                        {
                                                IDataCursor cache_16Cursor = cache_16.getCursor();
                                                cache_16Cursor.destroy();
                                        }
                                CHEQUE_DEPOSITCursor.destroy();
                        }
                CachePartyCursor.destroy();
        }
pipelineCursor.destroy();
 
// pipeline
IDataCursor pipelineCursor_1 = pipeline.getCursor();
 
// OPPPayments
IData	OPPPayments_1 = IDataFactory.create();
IDataCursor OPPPayments_1Cursor = OPPPayments_1.getCursor();
IDataUtil.put( OPPPayments_1Cursor, "ID", "ID" );
IDataUtil.put( OPPPayments_1Cursor, "UFE_ID", "UFE_ID" );
IDataUtil.put( OPPPayments_1Cursor, "DOMAIN_ID", "DOMAIN_ID" );
IDataUtil.put( OPPPayments_1Cursor, "TEMPLATE_ID", "TEMPLATE_ID" );
IDataUtil.put( OPPPayments_1Cursor, "PAYMENT_TYPE", "PAYMENT_TYPE" );
IDataUtil.put( OPPPayments_1Cursor, "PAYMENT_STATUS", "PAYMENT_STATUS" );
IDataUtil.put( OPPPayments_1Cursor, "PAYMENT_DETAILS", "PAYMENT_DETAILS" );
IDataUtil.put( OPPPayments_1Cursor, "CUSTOMER_REFERENCE", "CUSTOMER_REFERENCE" );
IDataUtil.put( OPPPayments_1Cursor, "BATCH_REFERENCE", "BATCH_REFERENCE" );
IDataUtil.put( OPPPayments_1Cursor, "DIRDEB_REFERENCE", "DIRDEB_REFERENCE" );
IDataUtil.put( OPPPayments_1Cursor, "ACCOUNT_ID", "ACCOUNT_ID" );
IDataUtil.put( OPPPayments_1Cursor, "BEN_ACCOUNT_NUMBER", "BEN_ACCOUNT_NUMBER" );
IDataUtil.put( OPPPayments_1Cursor, "BEN_ACCOUNT_NUMBER_TYPE", "BEN_ACCOUNT_NUMBER_TYPE" );
IDataUtil.put( OPPPayments_1Cursor, "TRANSFER_CCY", "TRANSFER_CCY" );
IDataUtil.put( OPPPayments_1Cursor, "TRANSFER_AMOUNT", "TRANSFER_AMOUNT" );
IDataUtil.put( OPPPayments_1Cursor, "TRANSFER_DATE", "TRANSFER_DATE" );
IDataUtil.put( OPPPayments_1Cursor, "CHARGES_FOR", "CHARGES_FOR" );
IDataUtil.put( OPPPayments_1Cursor, "BBI", "BBI" );
IDataUtil.put( OPPPayments_1Cursor, "SEND_CHEQUE_TO", "SEND_CHEQUE_TO" );
IDataUtil.put( OPPPayments_1Cursor, "CHECK_NR", "CHECK_NR" );
IDataUtil.put( OPPPayments_1Cursor, "CHEQUE_PRINTING_BRANCH", "CHEQUE_PRINTING_BRANCH" );
IDataUtil.put( OPPPayments_1Cursor, "FX_CONTRACT_NUMBER", "FX_CONTRACT_NUMBER" );
IDataUtil.put( OPPPayments_1Cursor, "FX_CONTRACT_DATE", "FX_CONTRACT_DATE" );
IDataUtil.put( OPPPayments_1Cursor, "FX_RATE", "FX_RATE" );
IDataUtil.put( OPPPayments_1Cursor, "PAYMENT_LOCATION", "PAYMENT_LOCATION" );
IDataUtil.put( OPPPayments_1Cursor, "INTERNAL_TRANSFER", "INTERNAL_TRANSFER" );
IDataUtil.put( OPPPayments_1Cursor, "DOMESTIC", "DOMESTIC" );
IDataUtil.put( OPPPayments_1Cursor, "INTRA_COMPANY", "INTRA_COMPANY" );
IDataUtil.put( OPPPayments_1Cursor, "INVOICE_DETAIL", "INVOICE_DETAIL" );
IDataUtil.put( OPPPayments_1Cursor, "INVOICE_BEN_EMAIL", "INVOICE_BEN_EMAIL" );
IDataUtil.put( OPPPayments_1Cursor, "INVOICE_BEN_EMAIL2", "INVOICE_BEN_EMAIL2" );
IDataUtil.put( OPPPayments_1Cursor, "ADD_CHEQUE_DETAIL1", "ADD_CHEQUE_DETAIL1" );
IDataUtil.put( OPPPayments_1Cursor, "ADD_CHEQUE_DETAIL2", "ADD_CHEQUE_DETAIL2" );
IDataUtil.put( OPPPayments_1Cursor, "ADD_CHEQUE_DETAIL3", "ADD_CHEQUE_DETAIL3" );
IDataUtil.put( OPPPayments_1Cursor, "ADD_CHEQUE_DETAIL4", "ADD_CHEQUE_DETAIL4" );
IDataUtil.put( OPPPayments_1Cursor, "SALARY_PAYMENT_TYPE", "SALARY_PAYMENT_TYPE" );
IDataUtil.put( OPPPayments_1Cursor, "PAYMENT_DATE", "PAYMENT_DATE" );
IDataUtil.put( OPPPayments_1Cursor, "CHEQUE_RECEIVE_MODE", "CHEQUE_RECEIVE_MODE" );
IDataUtil.put( OPPPayments_1Cursor, "CHEQUE_CRUZADO_TYPE", "CHEQUE_CRUZADO_TYPE" );
IDataUtil.put( OPPPayments_1Cursor, "CHEQUE_TRANSFER_DATE_TYPE", "CHEQUE_TRANSFER_DATE_TYPE" );
IDataUtil.put( OPPPayments_1Cursor, "PAYMENT_CATEGORY", "PAYMENT_CATEGORY" );
IDataUtil.put( OPPPayments_1Cursor, "CHEQUE_COLLECTION_BRANCH", "CHEQUE_COLLECTION_BRANCH" );
IDataUtil.put( OPPPayments_1Cursor, "NOTES_DETAIL", "NOTES_DETAIL" );
IDataUtil.put( OPPPayments_1Cursor, "DETAILS_OF_PAYMENT", "DETAILS_OF_PAYMENT" );
IDataUtil.put( OPPPayments_1Cursor, "RETENTION_FILE", "RETENTION_FILE" );
IDataUtil.put( OPPPayments_1Cursor, "INVOICE_BEN_FAX1", "INVOICE_BEN_FAX1" );
IDataUtil.put( OPPPayments_1Cursor, "INVOICE_BEN_FAX2", "INVOICE_BEN_FAX2" );
IDataUtil.put( OPPPayments_1Cursor, "INVOICE_BEN_CONTACT_NAME", "INVOICE_BEN_CONTACT_NAME" );
IDataUtil.put( OPPPayments_1Cursor, "INVOICE_BEN_NOTIFICATION_METHOD", "INVOICE_BEN_NOTIFICATION_METHOD" );
IDataUtil.put( OPPPayments_1Cursor, "DOCUMENTATION_DETAIL", "DOCUMENTATION_DETAIL" );
IDataUtil.put( OPPPayments_1Cursor, "VAT_AMOUNT", "VAT_AMOUNT" );
IDataUtil.put( OPPPayments_1Cursor, "VAT_CURRENCY", "VAT_CURRENCY" );
IDataUtil.put( OPPPayments_1Cursor, "ACCOUNT_CONTRACT_NO", "ACCOUNT_CONTRACT_NO" );
IDataUtil.put( OPPPayments_1Cursor, "CHEQUE_RECEIVER_ID", "CHEQUE_RECEIVER_ID" );
 
// OPPPayments.PARTY
IData[]	PARTY_1 = new IData[1];
PARTY_1[0] = IDataFactory.create();
IDataCursor PARTY_1Cursor = PARTY_1[0].getCursor();
IDataUtil.put( PARTY_1Cursor, "ID", "ID" );
IDataUtil.put( PARTY_1Cursor, "CODE", "CODE" );
IDataUtil.put( PARTY_1Cursor, "CONTEXT_ID", "CONTEXT_ID" );
IDataUtil.put( PARTY_1Cursor, "NAME", "NAME" );
IDataUtil.put( PARTY_1Cursor, "TYPE", "TYPE" );
IDataUtil.put( PARTY_1Cursor, "ADDRESS_1", "ADDRESS_1" );
IDataUtil.put( PARTY_1Cursor, "ADDRESS_2", "ADDRESS_2" );
IDataUtil.put( PARTY_1Cursor, "CITY", "CITY" );
IDataUtil.put( PARTY_1Cursor, "POSTAL_CODE", "POSTAL_CODE" );
IDataUtil.put( PARTY_1Cursor, "COUNTRY", "COUNTRY" );
IDataUtil.put( PARTY_1Cursor, "SWIFT_ID", "SWIFT_ID" );
IDataUtil.put( PARTY_1Cursor, "CLEARING_SYSTEM", "CLEARING_SYSTEM" );
IDataUtil.put( PARTY_1Cursor, "BANK_SORTING_CODE", "BANK_SORTING_CODE" );
IDataUtil.put( PARTY_1Cursor, "RESIDENT", "RESIDENT" );
IDataUtil.put( PARTY_1Cursor, "STATUS", "STATUS" );
IDataUtil.put( PARTY_1Cursor, "LOOKUP_ID", "LOOKUP_ID" );
IDataUtil.put( PARTY_1Cursor, "LOOKUP_MUTATION_ID", "LOOKUP_MUTATION_ID" );
IDataUtil.put( PARTY_1Cursor, "PARTY_TAX_ID", "PARTY_TAX_ID" );
IDataUtil.put( PARTY_1Cursor, "PARTY_ID_TYPE", "PARTY_ID_TYPE" );
IDataUtil.put( PARTY_1Cursor, "PARTY_ID_TYPE_NUMBER", "PARTY_ID_TYPE_NUMBER" );
IDataUtil.put( PARTY_1Cursor, "PARTY_ID_NUMBER", "PARTY_ID_NUMBER" );
IDataUtil.put( PARTY_1Cursor, "ACCOUNT_NUMBER", "ACCOUNT_NUMBER" );
IDataUtil.put( PARTY_1Cursor, "ACCOUNT_NUMBER_TYPE", "ACCOUNT_NUMBER_TYPE" );
IDataUtil.put( PARTY_1Cursor, "BANK_NAME", "BANK_NAME" );
IDataUtil.put( PARTY_1Cursor, "BANK_ADDRESS", "BANK_ADDRESS" );
IDataUtil.put( PARTY_1Cursor, "BANK_CITY", "BANK_CITY" );
 
// OPPPayments.PARTY.cache
IData	cache_17 = IDataFactory.create();
IDataCursor cache_17Cursor = cache_17.getCursor();
IDataUtil.put( cache_17Cursor, "isEnriched", "isEnriched" );
IDataUtil.put( cache_17Cursor, "ignoreMe", "ignoreMe" );
IDataUtil.put( cache_17Cursor, "Currency", "Currency" );
IDataUtil.put( cache_17Cursor, "CountryName", "CountryName" );
IDataUtil.put( cache_17Cursor, "BankKey", "BankKey" );
cache_17Cursor.destroy();
IDataUtil.put( PARTY_1Cursor, "cache", cache_17 );
PARTY_1Cursor.destroy();
IDataUtil.put( OPPPayments_1Cursor, "PARTY", PARTY_1 );
 
// OPPPayments.cache
IData	cache_18 = IDataFactory.create();
IDataCursor cache_18Cursor = cache_18.getCursor();
IDataUtil.put( cache_18Cursor, "isAllEnriched", "isAllEnriched" );
IDataUtil.put( cache_18Cursor, "isEnriched", "isEnriched" );
IDataUtil.put( cache_18Cursor, "ModelID", "ModelID" );
IDataUtil.put( cache_18Cursor, "DebitAccountKey", "DebitAccountKey" );
IDataUtil.put( cache_18Cursor, "DebitAccountCurrency", "DebitAccountCurrency" );
IDataUtil.put( cache_18Cursor, "TransactionType", "TransactionType" );
IDataUtil.put( cache_18Cursor, "BeneficiaryAccountKey", "BeneficiaryAccountKey" );
cache_18Cursor.destroy();
IDataUtil.put( OPPPayments_1Cursor, "cache", cache_18 );
OPPPayments_1Cursor.destroy();
IDataUtil.put( pipelineCursor_1, "OPPPayments", OPPPayments_1 );
 
// OPPPaymentsParty
IData	OPPPaymentsParty_1 = IDataFactory.create();
IDataCursor OPPPaymentsParty_1Cursor = OPPPaymentsParty_1.getCursor();
IDataUtil.put( OPPPaymentsParty_1Cursor, "ID", "ID" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "CODE", "CODE" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "CONTEXT_ID", "CONTEXT_ID" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "NAME", "NAME" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "TYPE", "TYPE" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "ADDRESS_1", "ADDRESS_1" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "ADDRESS_2", "ADDRESS_2" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "CITY", "CITY" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "POSTAL_CODE", "POSTAL_CODE" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "COUNTRY", "COUNTRY" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "SWIFT_ID", "SWIFT_ID" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "CLEARING_SYSTEM", "CLEARING_SYSTEM" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "BANK_SORTING_CODE", "BANK_SORTING_CODE" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "RESIDENT", "RESIDENT" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "STATUS", "STATUS" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "LOOKUP_ID", "LOOKUP_ID" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "LOOKUP_MUTATION_ID", "LOOKUP_MUTATION_ID" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "PARTY_TAX_ID", "PARTY_TAX_ID" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "PARTY_ID_TYPE", "PARTY_ID_TYPE" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "PARTY_ID_TYPE_NUMBER", "PARTY_ID_TYPE_NUMBER" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "PARTY_ID_NUMBER", "PARTY_ID_NUMBER" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "ACCOUNT_NUMBER", "ACCOUNT_NUMBER" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "ACCOUNT_NUMBER_TYPE", "ACCOUNT_NUMBER_TYPE" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "BANK_NAME", "BANK_NAME" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "BANK_ADDRESS", "BANK_ADDRESS" );
IDataUtil.put( OPPPaymentsParty_1Cursor, "BANK_CITY", "BANK_CITY" );
 
// OPPPaymentsParty.cache
IData	cache_19 = IDataFactory.create();
IDataCursor cache_19Cursor = cache_19.getCursor();
IDataUtil.put( cache_19Cursor, "isEnriched", "isEnriched" );
IDataUtil.put( cache_19Cursor, "ignoreMe", "ignoreMe" );
IDataUtil.put( cache_19Cursor, "Currency", "Currency" );
IDataUtil.put( cache_19Cursor, "CountryName", "CountryName" );
IDataUtil.put( cache_19Cursor, "BankKey", "BankKey" );
cache_19Cursor.destroy();
IDataUtil.put( OPPPaymentsParty_1Cursor, "cache", cache_19 );
OPPPaymentsParty_1Cursor.destroy();
IDataUtil.put( pipelineCursor_1, "OPPPaymentsParty", OPPPaymentsParty_1 );
 
// CacheParty
IData	CacheParty_1 = IDataFactory.create();
IDataCursor CacheParty_1Cursor = CacheParty_1.getCursor();
 
// CacheParty.BENEFICIARY
IData	BENEFICIARY_1 = IDataFactory.create();
IDataCursor BENEFICIARY_1Cursor = BENEFICIARY_1.getCursor();
IDataUtil.put( BENEFICIARY_1Cursor, "ID", "ID" );
IDataUtil.put( BENEFICIARY_1Cursor, "CODE", "CODE" );
IDataUtil.put( BENEFICIARY_1Cursor, "CONTEXT_ID", "CONTEXT_ID" );
IDataUtil.put( BENEFICIARY_1Cursor, "NAME", "NAME" );
IDataUtil.put( BENEFICIARY_1Cursor, "TYPE", "TYPE" );
IDataUtil.put( BENEFICIARY_1Cursor, "ADDRESS_1", "ADDRESS_1" );
IDataUtil.put( BENEFICIARY_1Cursor, "ADDRESS_2", "ADDRESS_2" );
IDataUtil.put( BENEFICIARY_1Cursor, "CITY", "CITY" );
IDataUtil.put( BENEFICIARY_1Cursor, "POSTAL_CODE", "POSTAL_CODE" );
IDataUtil.put( BENEFICIARY_1Cursor, "COUNTRY", "COUNTRY" );
IDataUtil.put( BENEFICIARY_1Cursor, "SWIFT_ID", "SWIFT_ID" );
IDataUtil.put( BENEFICIARY_1Cursor, "CLEARING_SYSTEM", "CLEARING_SYSTEM" );
IDataUtil.put( BENEFICIARY_1Cursor, "BANK_SORTING_CODE", "BANK_SORTING_CODE" );
IDataUtil.put( BENEFICIARY_1Cursor, "RESIDENT", "RESIDENT" );
IDataUtil.put( BENEFICIARY_1Cursor, "STATUS", "STATUS" );
IDataUtil.put( BENEFICIARY_1Cursor, "LOOKUP_ID", "LOOKUP_ID" );
IDataUtil.put( BENEFICIARY_1Cursor, "LOOKUP_MUTATION_ID", "LOOKUP_MUTATION_ID" );
IDataUtil.put( BENEFICIARY_1Cursor, "PARTY_TAX_ID", "PARTY_TAX_ID" );
IDataUtil.put( BENEFICIARY_1Cursor, "PARTY_ID_TYPE", "PARTY_ID_TYPE" );
IDataUtil.put( BENEFICIARY_1Cursor, "PARTY_ID_TYPE_NUMBER", "PARTY_ID_TYPE_NUMBER" );
IDataUtil.put( BENEFICIARY_1Cursor, "PARTY_ID_NUMBER", "PARTY_ID_NUMBER" );
IDataUtil.put( BENEFICIARY_1Cursor, "ACCOUNT_NUMBER", "ACCOUNT_NUMBER" );
IDataUtil.put( BENEFICIARY_1Cursor, "ACCOUNT_NUMBER_TYPE", "ACCOUNT_NUMBER_TYPE" );
IDataUtil.put( BENEFICIARY_1Cursor, "BANK_NAME", "BANK_NAME" );
IDataUtil.put( BENEFICIARY_1Cursor, "BANK_ADDRESS", "BANK_ADDRESS" );
IDataUtil.put( BENEFICIARY_1Cursor, "BANK_CITY", "BANK_CITY" );
 
// CacheParty.BENEFICIARY.cache
IData	cache_20 = IDataFactory.create();
IDataCursor cache_20Cursor = cache_20.getCursor();
IDataUtil.put( cache_20Cursor, "isEnriched", "isEnriched" );
IDataUtil.put( cache_20Cursor, "ignoreMe", "ignoreMe" );
IDataUtil.put( cache_20Cursor, "Currency", "Currency" );
IDataUtil.put( cache_20Cursor, "CountryName", "CountryName" );
IDataUtil.put( cache_20Cursor, "BankKey", "BankKey" );
cache_20Cursor.destroy();
IDataUtil.put( BENEFICIARY_1Cursor, "cache", cache_20 );
BENEFICIARY_1Cursor.destroy();
IDataUtil.put( CacheParty_1Cursor, "BENEFICIARY", BENEFICIARY_1 );
 
// CacheParty.BENEFICIARY_INSTITUTION
IData	BENEFICIARY_INSTITUTION_1 = IDataFactory.create();
IDataCursor BENEFICIARY_INSTITUTION_1Cursor = BENEFICIARY_INSTITUTION_1.getCursor();
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "ID", "ID" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "CODE", "CODE" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "CONTEXT_ID", "CONTEXT_ID" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "NAME", "NAME" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "TYPE", "TYPE" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "ADDRESS_1", "ADDRESS_1" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "ADDRESS_2", "ADDRESS_2" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "CITY", "CITY" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "POSTAL_CODE", "POSTAL_CODE" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "COUNTRY", "COUNTRY" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "SWIFT_ID", "SWIFT_ID" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "CLEARING_SYSTEM", "CLEARING_SYSTEM" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "BANK_SORTING_CODE", "BANK_SORTING_CODE" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "RESIDENT", "RESIDENT" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "STATUS", "STATUS" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "LOOKUP_ID", "LOOKUP_ID" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "LOOKUP_MUTATION_ID", "LOOKUP_MUTATION_ID" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "PARTY_TAX_ID", "PARTY_TAX_ID" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "PARTY_ID_TYPE", "PARTY_ID_TYPE" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "PARTY_ID_TYPE_NUMBER", "PARTY_ID_TYPE_NUMBER" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "PARTY_ID_NUMBER", "PARTY_ID_NUMBER" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "ACCOUNT_NUMBER", "ACCOUNT_NUMBER" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "ACCOUNT_NUMBER_TYPE", "ACCOUNT_NUMBER_TYPE" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "BANK_NAME", "BANK_NAME" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "BANK_ADDRESS", "BANK_ADDRESS" );
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "BANK_CITY", "BANK_CITY" );
 
// CacheParty.BENEFICIARY_INSTITUTION.cache
IData	cache_21 = IDataFactory.create();
IDataCursor cache_21Cursor = cache_21.getCursor();
IDataUtil.put( cache_21Cursor, "isEnriched", "isEnriched" );
IDataUtil.put( cache_21Cursor, "ignoreMe", "ignoreMe" );
IDataUtil.put( cache_21Cursor, "Currency", "Currency" );
IDataUtil.put( cache_21Cursor, "CountryName", "CountryName" );
IDataUtil.put( cache_21Cursor, "BankKey", "BankKey" );
cache_21Cursor.destroy();
IDataUtil.put( BENEFICIARY_INSTITUTION_1Cursor, "cache", cache_21 );
BENEFICIARY_INSTITUTION_1Cursor.destroy();
IDataUtil.put( CacheParty_1Cursor, "BENEFICIARY_INSTITUTION", BENEFICIARY_INSTITUTION_1 );
 
// CacheParty.CHEQUE_RECEIVER
IData	CHEQUE_RECEIVER_1 = IDataFactory.create();
IDataCursor CHEQUE_RECEIVER_1Cursor = CHEQUE_RECEIVER_1.getCursor();
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "ID", "ID" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "CODE", "CODE" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "CONTEXT_ID", "CONTEXT_ID" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "NAME", "NAME" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "TYPE", "TYPE" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "ADDRESS_1", "ADDRESS_1" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "ADDRESS_2", "ADDRESS_2" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "CITY", "CITY" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "POSTAL_CODE", "POSTAL_CODE" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "COUNTRY", "COUNTRY" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "SWIFT_ID", "SWIFT_ID" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "CLEARING_SYSTEM", "CLEARING_SYSTEM" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "BANK_SORTING_CODE", "BANK_SORTING_CODE" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "RESIDENT", "RESIDENT" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "STATUS", "STATUS" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "LOOKUP_ID", "LOOKUP_ID" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "LOOKUP_MUTATION_ID", "LOOKUP_MUTATION_ID" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "PARTY_TAX_ID", "PARTY_TAX_ID" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "PARTY_ID_TYPE", "PARTY_ID_TYPE" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "PARTY_ID_TYPE_NUMBER", "PARTY_ID_TYPE_NUMBER" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "PARTY_ID_NUMBER", "PARTY_ID_NUMBER" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "ACCOUNT_NUMBER", "ACCOUNT_NUMBER" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "ACCOUNT_NUMBER_TYPE", "ACCOUNT_NUMBER_TYPE" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "BANK_NAME", "BANK_NAME" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "BANK_ADDRESS", "BANK_ADDRESS" );
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "BANK_CITY", "BANK_CITY" );
 
// CacheParty.CHEQUE_RECEIVER.cache
IData	cache_22 = IDataFactory.create();
IDataCursor cache_22Cursor = cache_22.getCursor();
IDataUtil.put( cache_22Cursor, "isEnriched", "isEnriched" );
IDataUtil.put( cache_22Cursor, "ignoreMe", "ignoreMe" );
IDataUtil.put( cache_22Cursor, "Currency", "Currency" );
IDataUtil.put( cache_22Cursor, "CountryName", "CountryName" );
IDataUtil.put( cache_22Cursor, "BankKey", "BankKey" );
cache_22Cursor.destroy();
IDataUtil.put( CHEQUE_RECEIVER_1Cursor, "cache", cache_22 );
CHEQUE_RECEIVER_1Cursor.destroy();
IDataUtil.put( CacheParty_1Cursor, "CHEQUE_RECEIVER", CHEQUE_RECEIVER_1 );
 
// CacheParty.ACCOUNT_WITH_INSTITUTION
IData	ACCOUNT_WITH_INSTITUTION_1 = IDataFactory.create();
IDataCursor ACCOUNT_WITH_INSTITUTION_1Cursor = ACCOUNT_WITH_INSTITUTION_1.getCursor();
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "ID", "ID" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "CODE", "CODE" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "CONTEXT_ID", "CONTEXT_ID" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "NAME", "NAME" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "TYPE", "TYPE" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "ADDRESS_1", "ADDRESS_1" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "ADDRESS_2", "ADDRESS_2" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "CITY", "CITY" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "POSTAL_CODE", "POSTAL_CODE" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "COUNTRY", "COUNTRY" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "SWIFT_ID", "SWIFT_ID" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "CLEARING_SYSTEM", "CLEARING_SYSTEM" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "BANK_SORTING_CODE", "BANK_SORTING_CODE" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "RESIDENT", "RESIDENT" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "STATUS", "STATUS" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "LOOKUP_ID", "LOOKUP_ID" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "LOOKUP_MUTATION_ID", "LOOKUP_MUTATION_ID" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "PARTY_TAX_ID", "PARTY_TAX_ID" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "PARTY_ID_TYPE", "PARTY_ID_TYPE" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "PARTY_ID_TYPE_NUMBER", "PARTY_ID_TYPE_NUMBER" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "PARTY_ID_NUMBER", "PARTY_ID_NUMBER" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "ACCOUNT_NUMBER", "ACCOUNT_NUMBER" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "ACCOUNT_NUMBER_TYPE", "ACCOUNT_NUMBER_TYPE" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "BANK_NAME", "BANK_NAME" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "BANK_ADDRESS", "BANK_ADDRESS" );
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "BANK_CITY", "BANK_CITY" );
 
// CacheParty.ACCOUNT_WITH_INSTITUTION.cache
IData	cache_23 = IDataFactory.create();
IDataCursor cache_23Cursor = cache_23.getCursor();
IDataUtil.put( cache_23Cursor, "isEnriched", "isEnriched" );
IDataUtil.put( cache_23Cursor, "ignoreMe", "ignoreMe" );
IDataUtil.put( cache_23Cursor, "Currency", "Currency" );
IDataUtil.put( cache_23Cursor, "CountryName", "CountryName" );
IDataUtil.put( cache_23Cursor, "BankKey", "BankKey" );
cache_23Cursor.destroy();
IDataUtil.put( ACCOUNT_WITH_INSTITUTION_1Cursor, "cache", cache_23 );
ACCOUNT_WITH_INSTITUTION_1Cursor.destroy();
IDataUtil.put( CacheParty_1Cursor, "ACCOUNT_WITH_INSTITUTION", ACCOUNT_WITH_INSTITUTION_1 );
 
// CacheParty.DEBIT_BANK
IData	DEBIT_BANK_1 = IDataFactory.create();
IDataCursor DEBIT_BANK_1Cursor = DEBIT_BANK_1.getCursor();
IDataUtil.put( DEBIT_BANK_1Cursor, "ID", "ID" );
IDataUtil.put( DEBIT_BANK_1Cursor, "CODE", "CODE" );
IDataUtil.put( DEBIT_BANK_1Cursor, "CONTEXT_ID", "CONTEXT_ID" );
IDataUtil.put( DEBIT_BANK_1Cursor, "NAME", "NAME" );
IDataUtil.put( DEBIT_BANK_1Cursor, "TYPE", "TYPE" );
IDataUtil.put( DEBIT_BANK_1Cursor, "ADDRESS_1", "ADDRESS_1" );
IDataUtil.put( DEBIT_BANK_1Cursor, "ADDRESS_2", "ADDRESS_2" );
IDataUtil.put( DEBIT_BANK_1Cursor, "CITY", "CITY" );
IDataUtil.put( DEBIT_BANK_1Cursor, "POSTAL_CODE", "POSTAL_CODE" );
IDataUtil.put( DEBIT_BANK_1Cursor, "COUNTRY", "COUNTRY" );
IDataUtil.put( DEBIT_BANK_1Cursor, "SWIFT_ID", "SWIFT_ID" );
IDataUtil.put( DEBIT_BANK_1Cursor, "CLEARING_SYSTEM", "CLEARING_SYSTEM" );
IDataUtil.put( DEBIT_BANK_1Cursor, "BANK_SORTING_CODE", "BANK_SORTING_CODE" );
IDataUtil.put( DEBIT_BANK_1Cursor, "RESIDENT", "RESIDENT" );
IDataUtil.put( DEBIT_BANK_1Cursor, "STATUS", "STATUS" );
IDataUtil.put( DEBIT_BANK_1Cursor, "LOOKUP_ID", "LOOKUP_ID" );
IDataUtil.put( DEBIT_BANK_1Cursor, "LOOKUP_MUTATION_ID", "LOOKUP_MUTATION_ID" );
IDataUtil.put( DEBIT_BANK_1Cursor, "PARTY_TAX_ID", "PARTY_TAX_ID" );
IDataUtil.put( DEBIT_BANK_1Cursor, "PARTY_ID_TYPE", "PARTY_ID_TYPE" );
IDataUtil.put( DEBIT_BANK_1Cursor, "PARTY_ID_TYPE_NUMBER", "PARTY_ID_TYPE_NUMBER" );
IDataUtil.put( DEBIT_BANK_1Cursor, "PARTY_ID_NUMBER", "PARTY_ID_NUMBER" );
IDataUtil.put( DEBIT_BANK_1Cursor, "ACCOUNT_NUMBER", "ACCOUNT_NUMBER" );
IDataUtil.put( DEBIT_BANK_1Cursor, "ACCOUNT_NUMBER_TYPE", "ACCOUNT_NUMBER_TYPE" );
IDataUtil.put( DEBIT_BANK_1Cursor, "BANK_NAME", "BANK_NAME" );
IDataUtil.put( DEBIT_BANK_1Cursor, "BANK_ADDRESS", "BANK_ADDRESS" );
IDataUtil.put( DEBIT_BANK_1Cursor, "BANK_CITY", "BANK_CITY" );
 
// CacheParty.DEBIT_BANK.cache
IData	cache_24 = IDataFactory.create();
IDataCursor cache_24Cursor = cache_24.getCursor();
IDataUtil.put( cache_24Cursor, "isEnriched", "isEnriched" );
IDataUtil.put( cache_24Cursor, "ignoreMe", "ignoreMe" );
IDataUtil.put( cache_24Cursor, "Currency", "Currency" );
IDataUtil.put( cache_24Cursor, "CountryName", "CountryName" );
IDataUtil.put( cache_24Cursor, "BankKey", "BankKey" );
cache_24Cursor.destroy();
IDataUtil.put( DEBIT_BANK_1Cursor, "cache", cache_24 );
DEBIT_BANK_1Cursor.destroy();
IDataUtil.put( CacheParty_1Cursor, "DEBIT_BANK", DEBIT_BANK_1 );
 
// CacheParty.DEBIT_CUSTOMER
IData	DEBIT_CUSTOMER_1 = IDataFactory.create();
IDataCursor DEBIT_CUSTOMER_1Cursor = DEBIT_CUSTOMER_1.getCursor();
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "ID", "ID" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "CODE", "CODE" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "CONTEXT_ID", "CONTEXT_ID" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "NAME", "NAME" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "TYPE", "TYPE" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "ADDRESS_1", "ADDRESS_1" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "ADDRESS_2", "ADDRESS_2" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "CITY", "CITY" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "POSTAL_CODE", "POSTAL_CODE" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "COUNTRY", "COUNTRY" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "SWIFT_ID", "SWIFT_ID" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "CLEARING_SYSTEM", "CLEARING_SYSTEM" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "BANK_SORTING_CODE", "BANK_SORTING_CODE" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "RESIDENT", "RESIDENT" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "STATUS", "STATUS" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "LOOKUP_ID", "LOOKUP_ID" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "LOOKUP_MUTATION_ID", "LOOKUP_MUTATION_ID" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "PARTY_TAX_ID", "PARTY_TAX_ID" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "PARTY_ID_TYPE", "PARTY_ID_TYPE" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "PARTY_ID_TYPE_NUMBER", "PARTY_ID_TYPE_NUMBER" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "PARTY_ID_NUMBER", "PARTY_ID_NUMBER" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "ACCOUNT_NUMBER", "ACCOUNT_NUMBER" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "ACCOUNT_NUMBER_TYPE", "ACCOUNT_NUMBER_TYPE" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "BANK_NAME", "BANK_NAME" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "BANK_ADDRESS", "BANK_ADDRESS" );
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "BANK_CITY", "BANK_CITY" );
 
// CacheParty.DEBIT_CUSTOMER.cache
IData	cache_25 = IDataFactory.create();
IDataCursor cache_25Cursor = cache_25.getCursor();
IDataUtil.put( cache_25Cursor, "isEnriched", "isEnriched" );
IDataUtil.put( cache_25Cursor, "ignoreMe", "ignoreMe" );
IDataUtil.put( cache_25Cursor, "Currency", "Currency" );
IDataUtil.put( cache_25Cursor, "CountryName", "CountryName" );
IDataUtil.put( cache_25Cursor, "BankKey", "BankKey" );
cache_25Cursor.destroy();
IDataUtil.put( DEBIT_CUSTOMER_1Cursor, "cache", cache_25 );
DEBIT_CUSTOMER_1Cursor.destroy();
IDataUtil.put( CacheParty_1Cursor, "DEBIT_CUSTOMER", DEBIT_CUSTOMER_1 );
 
// CacheParty.INTERMEDIARY_INSTITUTION
IData	INTERMEDIARY_INSTITUTION_1 = IDataFactory.create();
IDataCursor INTERMEDIARY_INSTITUTION_1Cursor = INTERMEDIARY_INSTITUTION_1.getCursor();
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "ID", "ID" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "CODE", "CODE" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "CONTEXT_ID", "CONTEXT_ID" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "NAME", "NAME" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "TYPE", "TYPE" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "ADDRESS_1", "ADDRESS_1" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "ADDRESS_2", "ADDRESS_2" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "CITY", "CITY" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "POSTAL_CODE", "POSTAL_CODE" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "COUNTRY", "COUNTRY" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "SWIFT_ID", "SWIFT_ID" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "CLEARING_SYSTEM", "CLEARING_SYSTEM" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "BANK_SORTING_CODE", "BANK_SORTING_CODE" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "RESIDENT", "RESIDENT" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "STATUS", "STATUS" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "LOOKUP_ID", "LOOKUP_ID" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "LOOKUP_MUTATION_ID", "LOOKUP_MUTATION_ID" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "PARTY_TAX_ID", "PARTY_TAX_ID" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "PARTY_ID_TYPE", "PARTY_ID_TYPE" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "PARTY_ID_TYPE_NUMBER", "PARTY_ID_TYPE_NUMBER" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "PARTY_ID_NUMBER", "PARTY_ID_NUMBER" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "ACCOUNT_NUMBER", "ACCOUNT_NUMBER" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "ACCOUNT_NUMBER_TYPE", "ACCOUNT_NUMBER_TYPE" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "BANK_NAME", "BANK_NAME" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "BANK_ADDRESS", "BANK_ADDRESS" );
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "BANK_CITY", "BANK_CITY" );
 
// CacheParty.INTERMEDIARY_INSTITUTION.cache
IData	cache_26 = IDataFactory.create();
IDataCursor cache_26Cursor = cache_26.getCursor();
IDataUtil.put( cache_26Cursor, "isEnriched", "isEnriched" );
IDataUtil.put( cache_26Cursor, "ignoreMe", "ignoreMe" );
IDataUtil.put( cache_26Cursor, "Currency", "Currency" );
IDataUtil.put( cache_26Cursor, "CountryName", "CountryName" );
IDataUtil.put( cache_26Cursor, "BankKey", "BankKey" );
cache_26Cursor.destroy();
IDataUtil.put( INTERMEDIARY_INSTITUTION_1Cursor, "cache", cache_26 );
INTERMEDIARY_INSTITUTION_1Cursor.destroy();
IDataUtil.put( CacheParty_1Cursor, "INTERMEDIARY_INSTITUTION", INTERMEDIARY_INSTITUTION_1 );
 
// CacheParty.ORDERING_CUSTOMER
IData	ORDERING_CUSTOMER_1 = IDataFactory.create();
IDataCursor ORDERING_CUSTOMER_1Cursor = ORDERING_CUSTOMER_1.getCursor();
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "ID", "ID" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "CODE", "CODE" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "CONTEXT_ID", "CONTEXT_ID" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "NAME", "NAME" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "TYPE", "TYPE" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "ADDRESS_1", "ADDRESS_1" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "ADDRESS_2", "ADDRESS_2" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "CITY", "CITY" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "POSTAL_CODE", "POSTAL_CODE" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "COUNTRY", "COUNTRY" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "SWIFT_ID", "SWIFT_ID" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "CLEARING_SYSTEM", "CLEARING_SYSTEM" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "BANK_SORTING_CODE", "BANK_SORTING_CODE" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "RESIDENT", "RESIDENT" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "STATUS", "STATUS" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "LOOKUP_ID", "LOOKUP_ID" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "LOOKUP_MUTATION_ID", "LOOKUP_MUTATION_ID" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "PARTY_TAX_ID", "PARTY_TAX_ID" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "PARTY_ID_TYPE", "PARTY_ID_TYPE" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "PARTY_ID_TYPE_NUMBER", "PARTY_ID_TYPE_NUMBER" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "PARTY_ID_NUMBER", "PARTY_ID_NUMBER" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "ACCOUNT_NUMBER", "ACCOUNT_NUMBER" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "ACCOUNT_NUMBER_TYPE", "ACCOUNT_NUMBER_TYPE" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "BANK_NAME", "BANK_NAME" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "BANK_ADDRESS", "BANK_ADDRESS" );
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "BANK_CITY", "BANK_CITY" );
 
// CacheParty.ORDERING_CUSTOMER.cache
IData	cache_27 = IDataFactory.create();
IDataCursor cache_27Cursor = cache_27.getCursor();
IDataUtil.put( cache_27Cursor, "isEnriched", "isEnriched" );
IDataUtil.put( cache_27Cursor, "ignoreMe", "ignoreMe" );
IDataUtil.put( cache_27Cursor, "Currency", "Currency" );
IDataUtil.put( cache_27Cursor, "CountryName", "CountryName" );
IDataUtil.put( cache_27Cursor, "BankKey", "BankKey" );
cache_27Cursor.destroy();
IDataUtil.put( ORDERING_CUSTOMER_1Cursor, "cache", cache_27 );
ORDERING_CUSTOMER_1Cursor.destroy();
IDataUtil.put( CacheParty_1Cursor, "ORDERING_CUSTOMER", ORDERING_CUSTOMER_1 );
 
// CacheParty.ORDERING_INSTITUTION
IData	ORDERING_INSTITUTION_1 = IDataFactory.create();
IDataCursor ORDERING_INSTITUTION_1Cursor = ORDERING_INSTITUTION_1.getCursor();
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "ID", "ID" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "CODE", "CODE" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "CONTEXT_ID", "CONTEXT_ID" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "NAME", "NAME" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "TYPE", "TYPE" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "ADDRESS_1", "ADDRESS_1" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "ADDRESS_2", "ADDRESS_2" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "CITY", "CITY" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "POSTAL_CODE", "POSTAL_CODE" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "COUNTRY", "COUNTRY" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "SWIFT_ID", "SWIFT_ID" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "CLEARING_SYSTEM", "CLEARING_SYSTEM" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "BANK_SORTING_CODE", "BANK_SORTING_CODE" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "RESIDENT", "RESIDENT" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "STATUS", "STATUS" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "LOOKUP_ID", "LOOKUP_ID" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "LOOKUP_MUTATION_ID", "LOOKUP_MUTATION_ID" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "PARTY_TAX_ID", "PARTY_TAX_ID" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "PARTY_ID_TYPE", "PARTY_ID_TYPE" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "PARTY_ID_TYPE_NUMBER", "PARTY_ID_TYPE_NUMBER" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "PARTY_ID_NUMBER", "PARTY_ID_NUMBER" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "ACCOUNT_NUMBER", "ACCOUNT_NUMBER" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "ACCOUNT_NUMBER_TYPE", "ACCOUNT_NUMBER_TYPE" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "BANK_NAME", "BANK_NAME" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "BANK_ADDRESS", "BANK_ADDRESS" );
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "BANK_CITY", "BANK_CITY" );
 
// CacheParty.ORDERING_INSTITUTION.cache
IData	cache_28 = IDataFactory.create();
IDataCursor cache_28Cursor = cache_28.getCursor();
IDataUtil.put( cache_28Cursor, "isEnriched", "isEnriched" );
IDataUtil.put( cache_28Cursor, "ignoreMe", "ignoreMe" );
IDataUtil.put( cache_28Cursor, "Currency", "Currency" );
IDataUtil.put( cache_28Cursor, "CountryName", "CountryName" );
IDataUtil.put( cache_28Cursor, "BankKey", "BankKey" );
cache_28Cursor.destroy();
IDataUtil.put( ORDERING_INSTITUTION_1Cursor, "cache", cache_28 );
ORDERING_INSTITUTION_1Cursor.destroy();
IDataUtil.put( CacheParty_1Cursor, "ORDERING_INSTITUTION", ORDERING_INSTITUTION_1 );
 
// CacheParty.RECEIVERS_CORRESPONDENT
IData	RECEIVERS_CORRESPONDENT_1 = IDataFactory.create();
IDataCursor RECEIVERS_CORRESPONDENT_1Cursor = RECEIVERS_CORRESPONDENT_1.getCursor();
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "ID", "ID" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "CODE", "CODE" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "CONTEXT_ID", "CONTEXT_ID" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "NAME", "NAME" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "TYPE", "TYPE" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "ADDRESS_1", "ADDRESS_1" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "ADDRESS_2", "ADDRESS_2" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "CITY", "CITY" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "POSTAL_CODE", "POSTAL_CODE" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "COUNTRY", "COUNTRY" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "SWIFT_ID", "SWIFT_ID" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "CLEARING_SYSTEM", "CLEARING_SYSTEM" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "BANK_SORTING_CODE", "BANK_SORTING_CODE" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "RESIDENT", "RESIDENT" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "STATUS", "STATUS" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "LOOKUP_ID", "LOOKUP_ID" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "LOOKUP_MUTATION_ID", "LOOKUP_MUTATION_ID" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "PARTY_TAX_ID", "PARTY_TAX_ID" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "PARTY_ID_TYPE", "PARTY_ID_TYPE" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "PARTY_ID_TYPE_NUMBER", "PARTY_ID_TYPE_NUMBER" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "PARTY_ID_NUMBER", "PARTY_ID_NUMBER" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "ACCOUNT_NUMBER", "ACCOUNT_NUMBER" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "ACCOUNT_NUMBER_TYPE", "ACCOUNT_NUMBER_TYPE" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "BANK_NAME", "BANK_NAME" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "BANK_ADDRESS", "BANK_ADDRESS" );
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "BANK_CITY", "BANK_CITY" );
 
// CacheParty.RECEIVERS_CORRESPONDENT.cache
IData	cache_29 = IDataFactory.create();
IDataCursor cache_29Cursor = cache_29.getCursor();
IDataUtil.put( cache_29Cursor, "isEnriched", "isEnriched" );
IDataUtil.put( cache_29Cursor, "ignoreMe", "ignoreMe" );
IDataUtil.put( cache_29Cursor, "Currency", "Currency" );
IDataUtil.put( cache_29Cursor, "CountryName", "CountryName" );
IDataUtil.put( cache_29Cursor, "BankKey", "BankKey" );
cache_29Cursor.destroy();
IDataUtil.put( RECEIVERS_CORRESPONDENT_1Cursor, "cache", cache_29 );
RECEIVERS_CORRESPONDENT_1Cursor.destroy();
IDataUtil.put( CacheParty_1Cursor, "RECEIVERS_CORRESPONDENT", RECEIVERS_CORRESPONDENT_1 );
 
// CacheParty.SENDERS_CORRESPONDENT
IData	SENDERS_CORRESPONDENT_1 = IDataFactory.create();
IDataCursor SENDERS_CORRESPONDENT_1Cursor = SENDERS_CORRESPONDENT_1.getCursor();
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "ID", "ID" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "CODE", "CODE" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "CONTEXT_ID", "CONTEXT_ID" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "NAME", "NAME" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "TYPE", "TYPE" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "ADDRESS_1", "ADDRESS_1" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "ADDRESS_2", "ADDRESS_2" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "CITY", "CITY" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "POSTAL_CODE", "POSTAL_CODE" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "COUNTRY", "COUNTRY" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "SWIFT_ID", "SWIFT_ID" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "CLEARING_SYSTEM", "CLEARING_SYSTEM" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "BANK_SORTING_CODE", "BANK_SORTING_CODE" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "RESIDENT", "RESIDENT" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "STATUS", "STATUS" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "LOOKUP_ID", "LOOKUP_ID" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "LOOKUP_MUTATION_ID", "LOOKUP_MUTATION_ID" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "PARTY_TAX_ID", "PARTY_TAX_ID" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "PARTY_ID_TYPE", "PARTY_ID_TYPE" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "PARTY_ID_TYPE_NUMBER", "PARTY_ID_TYPE_NUMBER" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "PARTY_ID_NUMBER", "PARTY_ID_NUMBER" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "ACCOUNT_NUMBER", "ACCOUNT_NUMBER" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "ACCOUNT_NUMBER_TYPE", "ACCOUNT_NUMBER_TYPE" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "BANK_NAME", "BANK_NAME" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "BANK_ADDRESS", "BANK_ADDRESS" );
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "BANK_CITY", "BANK_CITY" );
 
// CacheParty.SENDERS_CORRESPONDENT.cache
IData	cache_30 = IDataFactory.create();
IDataCursor cache_30Cursor = cache_30.getCursor();
IDataUtil.put( cache_30Cursor, "isEnriched", "isEnriched" );
IDataUtil.put( cache_30Cursor, "ignoreMe", "ignoreMe" );
IDataUtil.put( cache_30Cursor, "Currency", "Currency" );
IDataUtil.put( cache_30Cursor, "CountryName", "CountryName" );
IDataUtil.put( cache_30Cursor, "BankKey", "BankKey" );
cache_30Cursor.destroy();
IDataUtil.put( SENDERS_CORRESPONDENT_1Cursor, "cache", cache_30 );
SENDERS_CORRESPONDENT_1Cursor.destroy();
IDataUtil.put( CacheParty_1Cursor, "SENDERS_CORRESPONDENT", SENDERS_CORRESPONDENT_1 );
 
// CacheParty.SENDING_INSTITUTION
IData	SENDING_INSTITUTION_1 = IDataFactory.create();
IDataCursor SENDING_INSTITUTION_1Cursor = SENDING_INSTITUTION_1.getCursor();
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "ID", "ID" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "CODE", "CODE" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "CONTEXT_ID", "CONTEXT_ID" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "NAME", "NAME" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "TYPE", "TYPE" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "ADDRESS_1", "ADDRESS_1" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "ADDRESS_2", "ADDRESS_2" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "CITY", "CITY" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "POSTAL_CODE", "POSTAL_CODE" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "COUNTRY", "COUNTRY" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "SWIFT_ID", "SWIFT_ID" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "CLEARING_SYSTEM", "CLEARING_SYSTEM" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "BANK_SORTING_CODE", "BANK_SORTING_CODE" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "RESIDENT", "RESIDENT" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "STATUS", "STATUS" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "LOOKUP_ID", "LOOKUP_ID" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "LOOKUP_MUTATION_ID", "LOOKUP_MUTATION_ID" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "PARTY_TAX_ID", "PARTY_TAX_ID" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "PARTY_ID_TYPE", "PARTY_ID_TYPE" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "PARTY_ID_TYPE_NUMBER", "PARTY_ID_TYPE_NUMBER" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "PARTY_ID_NUMBER", "PARTY_ID_NUMBER" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "ACCOUNT_NUMBER", "ACCOUNT_NUMBER" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "ACCOUNT_NUMBER_TYPE", "ACCOUNT_NUMBER_TYPE" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "BANK_NAME", "BANK_NAME" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "BANK_ADDRESS", "BANK_ADDRESS" );
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "BANK_CITY", "BANK_CITY" );
 
// CacheParty.SENDING_INSTITUTION.cache
IData	cache_31 = IDataFactory.create();
IDataCursor cache_31Cursor = cache_31.getCursor();
IDataUtil.put( cache_31Cursor, "isEnriched", "isEnriched" );
IDataUtil.put( cache_31Cursor, "ignoreMe", "ignoreMe" );
IDataUtil.put( cache_31Cursor, "Currency", "Currency" );
IDataUtil.put( cache_31Cursor, "CountryName", "CountryName" );
IDataUtil.put( cache_31Cursor, "BankKey", "BankKey" );
cache_31Cursor.destroy();
IDataUtil.put( SENDING_INSTITUTION_1Cursor, "cache", cache_31 );
SENDING_INSTITUTION_1Cursor.destroy();
IDataUtil.put( CacheParty_1Cursor, "SENDING_INSTITUTION", SENDING_INSTITUTION_1 );
 
// CacheParty.THIRD_REIMBURSEMENT_INST
IData	THIRD_REIMBURSEMENT_INST_1 = IDataFactory.create();
IDataCursor THIRD_REIMBURSEMENT_INST_1Cursor = THIRD_REIMBURSEMENT_INST_1.getCursor();
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "ID", "ID" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "CODE", "CODE" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "CONTEXT_ID", "CONTEXT_ID" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "NAME", "NAME" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "TYPE", "TYPE" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "ADDRESS_1", "ADDRESS_1" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "ADDRESS_2", "ADDRESS_2" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "CITY", "CITY" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "POSTAL_CODE", "POSTAL_CODE" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "COUNTRY", "COUNTRY" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "SWIFT_ID", "SWIFT_ID" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "CLEARING_SYSTEM", "CLEARING_SYSTEM" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "BANK_SORTING_CODE", "BANK_SORTING_CODE" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "RESIDENT", "RESIDENT" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "STATUS", "STATUS" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "LOOKUP_ID", "LOOKUP_ID" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "LOOKUP_MUTATION_ID", "LOOKUP_MUTATION_ID" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "PARTY_TAX_ID", "PARTY_TAX_ID" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "PARTY_ID_TYPE", "PARTY_ID_TYPE" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "PARTY_ID_TYPE_NUMBER", "PARTY_ID_TYPE_NUMBER" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "PARTY_ID_NUMBER", "PARTY_ID_NUMBER" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "ACCOUNT_NUMBER", "ACCOUNT_NUMBER" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "ACCOUNT_NUMBER_TYPE", "ACCOUNT_NUMBER_TYPE" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "BANK_NAME", "BANK_NAME" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "BANK_ADDRESS", "BANK_ADDRESS" );
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "BANK_CITY", "BANK_CITY" );
 
// CacheParty.THIRD_REIMBURSEMENT_INST.cache
IData	cache_32 = IDataFactory.create();
IDataCursor cache_32Cursor = cache_32.getCursor();
IDataUtil.put( cache_32Cursor, "isEnriched", "isEnriched" );
IDataUtil.put( cache_32Cursor, "ignoreMe", "ignoreMe" );
IDataUtil.put( cache_32Cursor, "Currency", "Currency" );
IDataUtil.put( cache_32Cursor, "CountryName", "CountryName" );
IDataUtil.put( cache_32Cursor, "BankKey", "BankKey" );
cache_32Cursor.destroy();
IDataUtil.put( THIRD_REIMBURSEMENT_INST_1Cursor, "cache", cache_32 );
THIRD_REIMBURSEMENT_INST_1Cursor.destroy();
IDataUtil.put( CacheParty_1Cursor, "THIRD_REIMBURSEMENT_INST", THIRD_REIMBURSEMENT_INST_1 );
 
// CacheParty.CHEQUE_DEPOSIT
IData	CHEQUE_DEPOSIT_1 = IDataFactory.create();
IDataCursor CHEQUE_DEPOSIT_1Cursor = CHEQUE_DEPOSIT_1.getCursor();
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "ID", "ID" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "CODE", "CODE" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "CONTEXT_ID", "CONTEXT_ID" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "NAME", "NAME" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "TYPE", "TYPE" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "ADDRESS_1", "ADDRESS_1" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "ADDRESS_2", "ADDRESS_2" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "CITY", "CITY" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "POSTAL_CODE", "POSTAL_CODE" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "COUNTRY", "COUNTRY" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "SWIFT_ID", "SWIFT_ID" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "CLEARING_SYSTEM", "CLEARING_SYSTEM" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "BANK_SORTING_CODE", "BANK_SORTING_CODE" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "RESIDENT", "RESIDENT" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "STATUS", "STATUS" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "LOOKUP_ID", "LOOKUP_ID" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "LOOKUP_MUTATION_ID", "LOOKUP_MUTATION_ID" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "PARTY_TAX_ID", "PARTY_TAX_ID" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "PARTY_ID_TYPE", "PARTY_ID_TYPE" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "PARTY_ID_TYPE_NUMBER", "PARTY_ID_TYPE_NUMBER" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "PARTY_ID_NUMBER", "PARTY_ID_NUMBER" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "ACCOUNT_NUMBER", "ACCOUNT_NUMBER" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "ACCOUNT_NUMBER_TYPE", "ACCOUNT_NUMBER_TYPE" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "BANK_NAME", "BANK_NAME" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "BANK_ADDRESS", "BANK_ADDRESS" );
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "BANK_CITY", "BANK_CITY" );
 
// CacheParty.CHEQUE_DEPOSIT.cache
IData	cache_33 = IDataFactory.create();
IDataCursor cache_33Cursor = cache_33.getCursor();
IDataUtil.put( cache_33Cursor, "isEnriched", "isEnriched" );
IDataUtil.put( cache_33Cursor, "ignoreMe", "ignoreMe" );
IDataUtil.put( cache_33Cursor, "Currency", "Currency" );
IDataUtil.put( cache_33Cursor, "CountryName", "CountryName" );
IDataUtil.put( cache_33Cursor, "BankKey", "BankKey" );
cache_33Cursor.destroy();
IDataUtil.put( CHEQUE_DEPOSIT_1Cursor, "cache", cache_33 );
CHEQUE_DEPOSIT_1Cursor.destroy();
IDataUtil.put( CacheParty_1Cursor, "CHEQUE_DEPOSIT", CHEQUE_DEPOSIT_1 );
CacheParty_1Cursor.destroy();
IDataUtil.put( pipelineCursor_1, "CacheParty", CacheParty_1 );
pipelineCursor_1.destroy();
 
    }
 */
    
    public static final void removePartyFromPartyListJava ( IData pipeline ) throws ServiceException {
        
/*
This service is a functional copy of the flow program removePartyFromPartyList
author   : G. Doets
date     : 2006 jan 30
release  : preformance improvements
 
Function :  This service loops over the input list and copies them to the outputList
            without party types in the parameter "removePartyType"
 
 */
        IDataCursor pipelineCursor = pipeline.getCursor ();
        IData[]   partiesListInput = IDataUtil.getIDataArray ( pipelineCursor, "partiesListInput" );
        
        if ( partiesListInput == null)    // if empty input array, skip everything
        {
            throw new ServiceException ("Exception from removePartyFromPartyList ===> Partylist is empty, throw  ServiceException ");
        }
        String removePartyType = IDataUtil.getString ( pipelineCursor, "removePartyType" );   //  type to be removed
        if (removePartyType == null) {
            // not a party to delete, the input array is completely copiied to the output
            IDataCursor pipelineCursor_1 = pipeline.getCursor ();
            IDataUtil.put ( pipelineCursor_1, "partiesListOutput", partiesListInput );
            pipelineCursor.destroy ();
        } else {
            
            // finding out how many will NOT be removed
            int numberPartiesNOTRemoved = 0;
            for ( int i = 0; i < partiesListInput.length; i++ ) {
                IDataCursor partiesListInputCursor = partiesListInput[i].getCursor ();
                String	TYPE = IDataUtil.getString ( partiesListInputCursor, "TYPE" );
                if (!removePartyType.equalsIgnoreCase (TYPE)) {
                    numberPartiesNOTRemoved++;
                }
            }
            
            
            // declare the outputArray wih size of numberPartiesNOTRemoved
            IData[]	partiesListOutput = new IData[numberPartiesNOTRemoved];
            int j = 0;  // counter for PartiesListOutput
            
            // looping over all the elements from the partielist
            //  and copy only those not equal to the removePartyType
            
            for ( int i = 0; i < partiesListInput.length; i++ ) {
                IDataCursor partiesListInputCursor = partiesListInput[i].getCursor ();
                String	TYPE = IDataUtil.getString ( partiesListInputCursor, "TYPE" );
                if (!removePartyType.equalsIgnoreCase (TYPE)) {
                    partiesListOutput[j++] = partiesListInput[i];
                    
                }
                partiesListInputCursor.destroy ();
            }
            IDataCursor pipelineCursor_1 = pipeline.getCursor ();
            
            IDataUtil.put ( pipelineCursor_1, "partiesListOutput", partiesListOutput );
            pipelineCursor_1.destroy ();
            
            
        }
        pipelineCursor.destroy ();
        
        
    }
    
    public static void main () throws ServiceException {
//    IDataCursor cur = new IDataCursor();
        String dateString = null;
        
//if (cur.first("dateString")) {
//  dateString = (String)cur.getValue();
//} else {
//	return;
//}
        
// cur.first("formatString");
// String formatString = ((String)cur.getValue()).trim();
        
        String		outFormatString = "yyyy-MM-dd HH:mm:ss.S";
        
        
        try {
 /*
        if (dateString.trim().equals("")) {
                cur.insertAfter("formattedDateString", "");
                return;
        }
  */
            Date temp;
            Date currentDate = new Date ();
            DateFormat inputFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss.S");
            DateFormat outputFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss.S");
            inputFormat.setLenient (false);
            temp = inputFormat.parse (dateString);
            if (( currentDate.getYear () - temp.getYear () >=  99  )
            || (temp.getYear () - currentDate.getYear ()  >= 99)) {
// diff is nore than 99 years, error
                throw new ServiceException ("error");
            }            String output = outputFormat.format (temp);
//	cur.insertAfter("formattedDateString", output);
        } catch (Exception e) { // In case of any error - blank out the date
            throw new ServiceException ("Could not parse date: " + dateString +
                "  Given format is :  yyyy-MM-dd HH:mm:ss.S" );
        }
        
    }
}
