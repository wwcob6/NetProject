/*
 * Copyright (C) 2005 Luca Veltri - University of Parma - Italy
 *
 * This file is part of MjSip (http://www.mjsip.org)
 *
 * MjSip is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * MjSip is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MjSip; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Author(s):
 * Luca Veltri (luca.veltri@unipr.it)
 */

package org.zoolu.sip.dialog;


import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import org.zoolu.sip.header.FromHeader;
import org.zoolu.sip.header.Header;
import org.zoolu.sip.header.RecordRouteHeader;
import org.zoolu.sip.header.ToHeader;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.provider.DialogId;
import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.SipProviderListener;
import org.zoolu.sip.provider.SipStack;
import org.zoolu.tools.ExceptionPrinter;
import org.zoolu.tools.Log;

import java.util.Vector;


/**
 * Class Dialog maintains a complete information status of a generic SIP dialog.
 * It has the following attributes:
 * <ul>
 * <li>sip-provider</li>
 * <li>call-id</li>
 * <li>local and remote URLs</li>
 * <li>local and remote contact URLs</li>
 * <li>local and remote cseqs</li>
 * <li>local and remote tags</li>
 * <li>dialog-id</li>
 * <li>route set</li>
 * </ul>
 */
public abstract class Dialog extends DialogInfo implements SipProviderListener {

    // ************************ Static attributes *************************

    /**
     * Identifier for the transaction client side of a dialog (UAC).
     */
    public final static int UAC = 0;
    /**
     * Identifier for the transaction server side of a dialog (UAS).
     */
    public final static int UAS = 1;

    /**
     * Dialogs counter
     */
    private static int dialog_counter = 0;


    // *********************** Protected attributes ***********************

    /**
     * Dialog sequence number
     */
    protected int dialog_sqn;

    /**
     * Log.
     */
    protected Log log;

    /**
     * SipProvider
     */
    protected SipProvider sip_provider;

    /**
     * Internal dialog state.
     */
    protected int status;

    /**
     * Dialog identifier
     */
    protected DialogId dialog_id;


    // ************************* Abstract methods *************************

    /**
     * Gets the dialog state
     */
    abstract protected String getStatus();

    /**
     * Whether the dialog is in "early" state.
     */
    abstract public boolean isEarly();

    /**
     * Whether the dialog is in "confirmed" state.
     */
    abstract public boolean isConfirmed();

    /**
     * Whether the dialog is in "terminated" state.
     */
    abstract public boolean isTerminated();

    /**
     * When a new Message is received by the SipProvider.
     */
    abstract public void onReceivedMessage(SipProvider provider, Message message);


    // **************************** Costructors ***************************

    /**
     * Creates a new empty Dialog
     */
    protected Dialog(SipProvider provider) {
        super();
        this.sip_provider = provider;
        this.log = sip_provider.getLog();
        this.dialog_sqn = dialog_counter++;
        this.status = 0;
        this.dialog_id = null;
    }


    // ************************* Protected methods ************************

    /**
     * Changes the internal dialog state
     */
    protected void changeStatus(int newstatus) {
        status = newstatus;
        printLog("changed dialog state: " + getStatus(), Log.LEVEL_MEDIUM);

        // remove the sip_provider listener when going to "terminated" state
        if (isTerminated()) {
            if (dialog_id != null && sip_provider.getListeners().containsKey(dialog_id))
                sip_provider.removeSelectiveListener(dialog_id);
        } else
            // add_icon sip_provider listener when going to "early" or "confirmed" state
            if (isEarly() || isConfirmed()) {
                if (dialog_id != null && !sip_provider.getListeners().containsKey(dialog_id))
                    sip_provider.addSelectiveListener(dialog_id, this);
            }
    }


    /**
     * Whether the dialog state is equal to <i>st</i>
     */
    protected boolean statusIs(int st) {
        return status == st;
    }


    // ************************** Public methods **************************

    /**
     * Gets the SipProvider of this Dialog.
     */
    public SipProvider getSipProvider() {
        return sip_provider;
    }


    /**
     * Gets the inique Dialog-ID </i>
     */
    public DialogId getDialogID() {
        return dialog_id;
    }


    /**
     * Updates empty attributes (tags, route set) and mutable attributes (cseqs, contacts), based on a new message.
     *
     * @param side indicates whether the Dialog is acting as transaction client or server for the current message (use constant values Dialog.UAC or Dialog.UAS)
     * @param msg  the message that is used to update the Dialog state
     */
    public void update(int side, Message msg) {
        if (isTerminated()) {
            printWarning("trying to update a terminated dialog: do nothing.", Log.LEVEL_HIGH);
            return;
        }
        // else

        // update call_id
        if (call_id == null) call_id = msg.getCallIdHeader().getCallId();

        // update names and tags
        if (side == UAC) {
            if (remote_name == null || remote_tag == null) {
                ToHeader to = msg.getToHeader();
                if (remote_name == null) remote_name = to.getNameAddress();
                if (remote_tag == null) remote_tag = to.getTag();
            }
            if (local_name == null || local_tag == null) {
                FromHeader from = msg.getFromHeader();
                if (local_name == null) local_name = from.getNameAddress();
                if (local_tag == null) local_tag = from.getTag();
            }
            local_cseq = msg.getCSeqHeader().getSequenceNumber();
            //if (remote_cseq==-1) remote_cseq=SipProvider.pickInitialCSeq()-1;
        } else {
            if (local_name == null || local_tag == null) {
                ToHeader to = msg.getToHeader();
                if (local_name == null) local_name = to.getNameAddress();
                if (local_tag == null) local_tag = to.getTag();
            }
            if (remote_name == null || remote_tag == null) {
                FromHeader from = msg.getFromHeader();
                if (remote_name == null) remote_name = from.getNameAddress();
                if (remote_tag == null) remote_tag = from.getTag();
            }
            remote_cseq = msg.getCSeqHeader().getSequenceNumber();
            if (local_cseq == -1) local_cseq = SipProvider.pickInitialCSeq() - 1;
        }
        // update contact
        if (msg.hasContactHeader()) {
            if ((side == UAC && msg.isRequest()) || (side == UAS && msg.isResponse()))
                local_contact = msg.getContactHeader().getNameAddress();
            else
                remote_contact = msg.getContactHeader().getNameAddress();
        }
        // update route or record-route
        if (side == UAC) {
            if (msg.isRequest() && msg.hasRouteHeader() && route == null) {
                route = msg.getRoutes().getValues();
            }
            if (side == UAC && msg.isResponse() && msg.hasRecordRouteHeader()) {
                Vector rr = msg.getRecordRoutes().getHeaders();
                int size = rr.size();
                route = new Vector(size);
                for (int i = 0; i < size; i++)
                    route.insertElementAt((new RecordRouteHeader((Header) rr.elementAt(size - 1 - i))).getNameAddress(), i);
            }
        } else {
            if (msg.isRequest() && msg.hasRouteHeader() && route == null) {
                Vector reverse_route = msg.getRoutes().getValues();
                int size = reverse_route.size();
                route = new Vector(size);
                for (int i = 0; i < size; i++)
                    route.insertElementAt(reverse_route.elementAt(size - 1 - i), i);
            }
            if (msg.isRequest() && msg.hasRecordRouteHeader()) {
                Vector rr = msg.getRecordRoutes().getHeaders();
                int size = rr.size();
                route = new Vector(size);
                for (int i = 0; i < size; i++)
                    route.insertElementAt((new RecordRouteHeader((Header) rr.elementAt(i))).getNameAddress(), i);
            }
        }
        // REMOVE THE LOCAL NODE FROM THE ROUTE SET (ELIMINATE FIRST-HOP LOOP)
        if (SipStack.on_dialog_route) {
            if (route != null && route.size() > 0) {
                SipURL url = ((NameAddress) route.elementAt(0)).getAddress();
                if (url.getHost().equals(sip_provider.getViaAddress()) && url.getPort() == sip_provider.getPort()) {
                    route.removeElementAt(0);
                }
            }
            if (route != null && route.size() > 0) {
                SipURL url = ((NameAddress) route.elementAt(route.size() - 1)).getAddress();
                if (url.getHost().equals(sip_provider.getViaAddress()) && url.getPort() == sip_provider.getPort()) {
                    route.removeElementAt(route.size() - 1);
                }
            }
        }
        // update dialog_id and sip_provider listener
        DialogId new_id = new DialogId(call_id, local_tag, remote_tag);
        if (dialog_id == null || !dialog_id.equals(new_id)) {
            printLog("new dialog-id: " + new_id, Log.LEVEL_HIGH);
            if (sip_provider != null) sip_provider.addSelectiveListener(new_id, this);
            if (dialog_id != null && sip_provider != null)
                sip_provider.removeSelectiveListener(dialog_id);
            dialog_id = new_id;
        }
        // update secure
        if (!secure && msg.isRequest()) {
            if (msg.getRequestLine().getAddress().isSecure() && msg.getViaHeader().getProtocol().equalsIgnoreCase("tls")) {
                secure = true;
                printLog("secure dialog: on", Log.LEVEL_HIGH);
            }
        }
    }


    //**************************** Logs ****************************/

    /**
     * Default log level offset
     */
    static final int LOG_OFFSET = 2;

    /**
     * Adds a new string to the default Log
     */
    protected void printLog(String str, int level) {
        if (log != null)
            log.println("Dialog#" + dialog_sqn + ": " + str, Dialog.LOG_OFFSET + level);
    }

    /**
     * Adds a Warning message to the default Log
     */
    protected final void printWarning(String str, int level) {
        printLog("WARNING: " + str, level);
    }

    /**
     * Adds the Exception message to the default Log
     */
    protected final void printException(Exception e, int level) {
        printLog("Exception: " + ExceptionPrinter.getStackTraceOf(e), level);
    }

    /**
     * Verifies the correct status; if not logs the event.
     */
    protected final boolean verifyStatus(boolean expression) {
        return verifyThat(expression, "dialog state mismatching");
    }

    /**
     * Verifies an event; if not logs it.
     */
    protected final boolean verifyThat(boolean expression, String str) {
        if (!expression) {
            if (str == null || str.length() == 0) printWarning("expression check failed. ", 1);
            else printWarning(str, 1);
        }
        return expression;
    }

}