package org.apache.log4j;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 按照日期时间戳滚动生成新的日志文件
 * Created by yinbp on 2017/6/22.
 * Modified by yinbp on 2020/8/12
 */
public class NormalRollingFileAppender  extends RollingFileAppender {

    protected String originFileName;

    public boolean isUseDatePattern() {
        return useDatePattern;
    }

    public void setUseDatePattern(boolean useDatePattern) {
        this.useDatePattern = useDatePattern;
    }

    protected boolean useDatePattern = true;

    /**
     * 
     */
    protected SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddHHmmss");


    protected long tnextRollover = 0;

    /**
     The default constructor simply calls its {@link
    FileAppender#FileAppender parents constructor}.  */
    public
    NormalRollingFileAppender() {
        super();
    }

    /**
     Instantiate a RollingFileAppender and open the file designated by
     <code>filename</code>. The opened filename will become the ouput
     destination for this appender.

     <p>If the <code>append</code> parameter is true, the file will be
     appended to. Otherwise, the file desginated by
     <code>filename</code> will be truncated before being opened.
     */
    public
    NormalRollingFileAppender(Layout layout, String filename, boolean append)
            throws IOException {
        super(layout, filename, append);
    }

    /**
     Instantiate a FileAppender and open the file designated by
     <code>filename</code>. The opened filename will become the output
     destination for this appender.

     <p>The file will be appended to.  */
    public
    NormalRollingFileAppender(Layout layout, String filename) throws IOException {
        super(layout, filename);
    }




    protected boolean deleteOldFile(){
        boolean renameSucceeded = true;
        if(!parent.exists()){
            return renameSucceeded;
        }
        File[] logfiles = parent.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if(name.startsWith(logFileName))
                    return true;
                return false;
            }
        });
        if(logfiles == null || logfiles.length == 0 || logfiles.length < this.maxBackupIndex)
            return renameSucceeded;
        File file = null;

        for(File f:logfiles){
            if(file == null)
                file = f;
            else if(file.lastModified() > f.lastModified()){
                file = f;
            }
        }
        if (file != null && file.exists())
            renameSucceeded = file.delete();
        return renameSucceeded;
    }

    /**
     Implements the usual roll over behaviour.

     <p>If <code>MaxBackupIndex</code> is positive, then files
     {<code>File.1</code>, ..., <code>File.MaxBackupIndex -1</code>}
     are renamed to {<code>File.2</code>, ...,
     <code>File.MaxBackupIndex</code>}. Moreover, <code>File</code> is
     renamed <code>File.1</code> and closed. A new <code>File</code> is
     created to receive further log output.

     <p>If <code>MaxBackupIndex</code> is equal to zero, then the
     <code>File</code> is truncated with no backup files created.

     */
    public // synchronization not necessary since doAppend is alreasy synched
    void rollOver() {

        File file;

        if (qw != null) {
            long size = ((CountingQuietWriter) qw).getCount();
            LogLog.debug("rolling over count=" + size);
            //   if operation fails, do not roll again until
            //      maxFileSize more bytes are written
            tnextRollover = size + maxFileSize;

        }

        LogLog.debug("maxBackupIndex="+maxBackupIndex);


        boolean renameSucceeded = true;
        if(maxBackupIndex > 0) {
            renameSucceeded = deleteOldFile();
        }
        if(!renameSucceeded){
            try {
                this.setFile(this.fileName, true, this.bufferedIO, this.bufferSize);
            } catch (IOException var6) {
                if(var6 instanceof InterruptedIOException) {
                    Thread.currentThread().interrupt();
                }

                LogLog.error("setFile(" + this.fileName + ", true) call failed.", var6);
            }
        }
        else {
            this.closeFile(); // keep windows happy.
            try {

                // This will also close the file. This is OK since multiple
                // close operations are safe.
                Date time = new Date(System.currentTimeMillis());
                String currentIndex = this.dataFormat.format(time);
                String tempfileName = this.originFileName + "." + currentIndex;
                this.fileName = tempfileName;
                this.setFile(fileName, false, bufferedIO, bufferSize);
                tnextRollover = 0;
            } catch (IOException e) {
                if (e instanceof InterruptedIOException) {
                    Thread.currentThread().interrupt();
                }
                LogLog.error("setFile(" + fileName + ", false) call failed.", e);
            }
        }


    }




    protected  String logFileName;
    protected String buildName(String name){
        return name;
    }

    protected File parent;
    public void setFile(String file) {
        String val = file.trim();
        val = this.buildName(val);
        int idx = val.lastIndexOf("/");
        parent = new File(val).getParentFile();
        if(parent == null)
            parent = new File(".");
        if(idx >= 0){
            logFileName = val.substring(idx+1);
        }
        else
            logFileName = val;
        logFileName = logFileName + ".";
        this.originFileName = val;

        if(!parent.exists()){
            Date time = new Date(System.currentTimeMillis());
            String currentIndex = this.dataFormat.format(time);
            this.fileName = val + "." + currentIndex;
        }
        else {//将fileName设置为最近文件路径
            File[] logfiles = parent.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    if (name.startsWith(logFileName))
                        return true;
                    return false;
                }
            });
            if(logfiles == null || logfiles.length == 0){
                Date time = new Date(System.currentTimeMillis());
                String currentIndex = this.dataFormat.format(time);
                this.fileName = val + "." + currentIndex;
            }
            else
            {
                File lastFile = null;
                for(File f:logfiles){
                    if(lastFile == null)
                        lastFile = f;
                    else if(f.lastModified() > lastFile.lastModified()){
                        lastFile = f;
                    }
                }
                try {
                    this.fileName = lastFile.getCanonicalPath();
                } catch (IOException e) {
                    e.printStackTrace();
                    Date time = new Date(System.currentTimeMillis());
                    String currentIndex = this.dataFormat.format(time);
                    this.fileName = val + "." + currentIndex;
                }
            }

        }


    }





    /**
     This method differentiates RollingFileAppender from its super
     class.

     @since 0.9.0
     */
    protected
    void subAppend(LoggingEvent event) {
        super.subAppend(event);
        if(fileName != null && qw != null) {
            long size = ((CountingQuietWriter) qw).getCount();
            if (size >= maxFileSize && size >= tnextRollover) {
                rollOver();
            }
        }
    }
}
