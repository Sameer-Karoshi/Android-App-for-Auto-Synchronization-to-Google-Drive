package com.sameer.mydrive.Object;

//This class is for folders on device storage(External Storage)
public class Folder {
    String folderName;
    String localAddress;
    Boolean[] days;
    String time;
    String lastSync;
    Boolean isSyncable;
    int status; // Status will be 0 if file is not syncing, it'll be 1 if it is syncing and -1 if its paused
    public Folder(String folderName, String localAddress, Boolean[] days, String time, String lastSync, Boolean isSyncable, int status) {
        this.folderName = folderName;
        this.localAddress = localAddress;
        this.days = days;
        this.time = time;
        this.lastSync = lastSync;
        this.isSyncable = isSyncable;
        this.status = status;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public Boolean[] getDays() {
        return days;
    }

    public void setDays(Boolean[] days) {
        this.days = days;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLastSync() {
        return lastSync;
    }

    public void setLastSync(String lastSync) {
        this.lastSync = lastSync;
    }

    public Boolean getSyncable() {
        return isSyncable;
    }

    public void setSyncable(Boolean syncable) {
        isSyncable = syncable;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}


