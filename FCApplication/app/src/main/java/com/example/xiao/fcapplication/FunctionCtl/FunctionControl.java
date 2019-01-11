package com.example.xiao.fcapplication.FunctionCtl;



import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;


import java.util.List;

import vr.suntec.net.vrapp.aidl.IVRAidlService;

import static android.content.Context.BIND_AUTO_CREATE;

public class FunctionControl {
    private static FunctionControl funcctl;
    IVRAidlService m_Service = null;
    private Context mContext = null;

    public static FunctionControl getInstance(Context context)
    {
        if(funcctl == null) {
            funcctl = new FunctionControl(context);
        }
        return funcctl;
    }
    public void onCreate(Context context) {

    }

    public void onStart() {
        this.connectService();
        Log.d("VRLogTag","connect success");
    }

    public void startVR() throws RemoteException {
        if(m_Service != null) {
            m_Service.startVR();
        }
        else {
            m_Service = getVrService();
            m_Service.startVR();
        }
    }


    public FunctionControl(Context mContext)
    {
        this.mContext = mContext;
    }

    public IVRAidlService getVrService()
    {
        Log.d("VRLogTag","getBookService");
        while (m_Service == null)
        {
            Log.d("VRLogTag","getBookService NULL");
            this.connectService();
        }
        return m_Service;
    }

    ServiceConnection scc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("VRLogTag", "getVrService:2 ==> Bind");
            m_Service = IVRAidlService.Stub.asInterface(service);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            m_Service = null;
        }
    };

    public boolean connectService()
    {
        if(m_Service == null)
        {
            Log.d("VRLogTag","getVrServie: 2");
            Intent intent = new Intent("vr.suntec.net.vrapp.aidl.start_service");
            final Intent eintent = new Intent(createExplicitFromImplicitIntent(mContext,intent));
            mContext.bindService(eintent, scc, BIND_AUTO_CREATE);
        }
        return true;
    }

    public static Intent createExplicitFromImplicitIntent(Context context,Intent implicitIntent)
    {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent,0);
        // Make sure only one match was found
        if(resolveInfo == null || resolveInfo.size() != 1)
        {
            return null;
        }
        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);
        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }

}
