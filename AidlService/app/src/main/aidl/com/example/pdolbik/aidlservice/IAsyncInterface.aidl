// IAsyncInterface.aidl
package com.example.pdolbik.aidlservice;

import com.example.pdolbik.aidlservice.IAsyncCallbacklInterface;

interface IAsyncInterface {

    //Async method
    oneway void doLongTask(int i, IAsyncCallbacklInterface callback);
}

