package com.sst.devtools.alterproxy.swingui;

import com.sst.devtools.alterproxy.LFSMapEntry;

public interface IMapEntryEditorNotifier {
    LFSMapEntry getSourceEntry();

    void notifyNewEntry(LFSMapEntry newEntry);
}
