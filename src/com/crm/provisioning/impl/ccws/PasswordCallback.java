package com.crm.provisioning.impl.ccws;

import java.io.*;
import javax.security.auth.callback.*;
import javax.security.auth.callback.Callback;

import org.apache.ws.security.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @version 1.0
 */
public class PasswordCallback implements CallbackHandler {

        /**
         *
         */
        String mstrPassWord = null;

        public PasswordCallback() {
//            mstrPassWord = "epos123456";
        }

        public PasswordCallback(String pstrPassWord) {
            mstrPassWord = pstrPassWord;
        }
        /**
         * PasswordCallback
         *
         * @param cCWSDispatcherSplitThread CCWSDispatcherSplitThread
         */

        /**
         *
         * @param callbacks Callback[]
         * @throws IOException
         * @throws UnsupportedCallbackException
         */
        public void handle(Callback[] callbacks) throws IOException,
                        UnsupportedCallbackException {
                for (int i = 0; i < callbacks.length; i++) {
                        WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
//                        pc.setPassword("epos123456");
                        pc.setPassword(mstrPassWord);
                }
        }
}
