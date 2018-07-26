package com.greece.nasiakouts.babysitterfinder.Widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class BabysitterFinderService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new BabysitterFinderRemoteViewsFactory(this.getApplicationContext(), intent));
    }
}
