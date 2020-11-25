package com.punuo.sys.sdk.util;

import android.text.TextUtils;

import com.google.gson.JsonParseException;
import com.punuo.sys.sdk.R;
import com.punuo.sys.sdk.httplib.ErrorTipException;

import org.apache.http.conn.ConnectTimeoutException;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class HandlerExceptionUtils {

    public static void handleException(Exception exception) {
        int err;
        try {
            throw exception;
        } catch (UnknownHostException e) {
            err = R.string.error_no_net;
        } catch (SocketTimeoutException | ConnectTimeoutException e) {
            err = R.string.error_timeout;
        } catch (JsonParseException e) {
            err = R.string.error_json_parse_exception;
        } catch (NullPointerException e) {
            err = R.string.error_json;
        } catch (ErrorTipException e) {
            String message = e.getMessage();
            ToastUtils.showToast(message);
            return;
        } catch (Exception e) {
            String message = e.getMessage();
            if (!TextUtils.isEmpty(message) && (message.contains("Socket closed") || message.contains("Canceled"))) {
                return;
            }
            err = R.string.error_unknown;
        }
        ToastUtils.showToast(err);
    }
}
