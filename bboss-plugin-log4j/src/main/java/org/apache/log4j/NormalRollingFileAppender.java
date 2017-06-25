package org.apache.log4j;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 1 on 2017/6/22.
 */
public class NormalRollingFileAppender  extends FileAppender {
    public String getOriginFileName() {
        return originFileName;
    }

    public void setOriginFileName(String originFileName) {
        this.originFileName = originFileName;
    }

    private String originFileName;
    /**
     The default maximum file size is 10MB.
     */
    protected long maxFileSize = 10*1024*1024;
    /**
     * 当前文件索引号
     */
    protected int currentIndex = 1;
    protected String datePattern = "yyyyMMddHHmmss";
    protected boolean useDatePattern = false;
    /**
     * 
     */
    protected SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    /**
     There is one backup file by default.
     */
    protected int  maxBackupIndex  = 1;

    private long nextRollover = 0;

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

    /**
     Returns the value of the <b>MaxBackupIndex</b> option.
     */
    public
    int getMaxBackupIndex() {
        return maxBackupIndex;
    }

    /**
     Get the maximum size that the output file is allowed to reach
     before being rolled over to backup files.

     @since 1.1
     */
    public
    long getMaximumFileSize() {
        return maxFileSize;
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
            nextRollover = size + maxFileSize;

        }

        LogLog.debug("maxBackupIndex="+maxBackupIndex);
        if(!useDatePattern){
            boolean max = false;
	        boolean renameSucceeded = true;
	        if(this.currentIndex == Integer.MAX_VALUE)//达到最大值，重新从1开始
            {
                currentIndex = 1;
                max = true;
            }
	        else
	            currentIndex ++;
	        // If maxBackups <= 0, then there is no file renaming to be done.
	        if(maxBackupIndex > 0) {

                int delIndex = currentIndex - 1 - maxBackupIndex;
                // Delete the oldest file, to keep Windows happy.
                if (delIndex > 0) {
                    file = new File(this.originFileName + '.' + delIndex);
                    if (file.exists())
                        renameSucceeded = file.delete();
                }

	
	        }
	        if(!renameSucceeded) {
	            try {
	                if(max){
                        this.currentIndex = Integer.MAX_VALUE;
                    }
                    else
	                    currentIndex --;
	                this.setFile(this.fileName, true, this.bufferedIO, this.bufferSize);
	            } catch (IOException var6) {
	                if(var6 instanceof InterruptedIOException) {
	                    Thread.currentThread().interrupt();
	                }
	
	                LogLog.error("setFile(" + this.fileName + ", true) call failed.", var6);
	            }
	        }
	        else         
	        {

	            try {
	                // This will also close the file. This is OK since multiple
	                // close operations are safe.
	                String tempfileName = this.originFileName + "."+currentIndex;
	                this.fileName = tempfileName;
	                this.setFile(fileName, false, bufferedIO, bufferSize);
	                nextRollover = 0;
	            } catch (IOException e) {
	                if (e instanceof InterruptedIOException) {
	                    Thread.currentThread().interrupt();
	                }
	                LogLog.error("setFile(" + fileName + ", false) call failed.", e);
	            }
	        }
        }
        else
        {
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
                try {
                    // This will also close the file. This is OK since multiple
                    // close operations are safe.
                    Date time = new Date(System.currentTimeMillis());
                    String currentIndex = this.dataFormat.format(time);
                    String tempfileName = this.originFileName + "." + currentIndex;
                    this.fileName = tempfileName;
                    this.setFile(fileName, false, bufferedIO, bufferSize);
                    nextRollover = 0;
                } catch (IOException e) {
                    if (e instanceof InterruptedIOException) {
                        Thread.currentThread().interrupt();
                    }
                    LogLog.error("setFile(" + fileName + ", false) call failed.", e);
                }
            }
        }

    }

    public
    synchronized
    void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
            throws IOException {
        super.setFile(fileName, append, this.bufferedIO, this.bufferSize);
        if(append) {
            File f = new File(fileName);
            ((CountingQuietWriter) qw).setCount(f.length());
        }
    }


    public boolean isUseDatePattern() {
        return useDatePattern;
    }
    protected  String logFileName;
    public void setUseDatePattern(boolean useDatePattern) {
        this.useDatePattern = useDatePattern;
    }
    protected File parent;
    public void setFile(String file) {
        String val = file.trim();
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
        if(!useDatePattern){//采用数字索引，计算当前的数字索引，默认索引为1

            String tempFileName = val + "." + currentIndex;

            if(!parent.exists()){
                this.fileName = tempFileName;
            }
            else
            {


                String[] files = parent.list(new java.io.FilenameFilter(){

                    public boolean accept(File dir, String name) {
                        if(name.startsWith(logFileName)){
                            return true;
                        }
                        return false;
                    }
                });
                if(files == null || files.length == 0){
                    this.fileName = tempFileName;
                }
                else{
                    int position = 1;
                    for(String f:files){
                        idx = f.lastIndexOf(".");
                        if(idx > 0){
                            String pos = f.substring(idx + 1);
                            try {
                                int ipos = Integer.parseInt(pos);
                                if(ipos > position){
                                    position = ipos;
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    this.currentIndex = position;
                    this.fileName = val + "." + currentIndex;
                }
            }
        }
        else {
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
    }



    /**
     Set the maximum number of backup files to keep around.

     <p>The <b>MaxBackupIndex</b> option determines how many backup
     files are kept before the oldest is erased. This option takes
     a positive integer value. If set to zero, then there will be no
     backup files and the log file will be truncated when it reaches
     <code>MaxFileSize</code>.
     */
    public
    void setMaxBackupIndex(int maxBackups) {
        this.maxBackupIndex = maxBackups;
    }

    /**
     Set the maximum size that the output file is allowed to reach
     before being rolled over to backup files.

     <p>This method is equivalent to {@link #setMaxFileSize} except
     that it is required for differentiating the setter taking a
     <code>long</code> argument from the setter taking a
     <code>String</code> argument by the JavaBeans {@link
    java.beans.Introspector Introspector}.

     @see #setMaxFileSize(String)
     */
    public
    void setMaximumFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }


    /**
     Set the maximum size that the output file is allowed to reach
     before being rolled over to backup files.

     <p>In configuration files, the <b>MaxFileSize</b> option takes an
     long integer in the range 0 - 2^63. You can specify the value
     with the suffixes "KB", "MB" or "GB" so that the integer is
     interpreted being expressed respectively in kilobytes, megabytes
     or gigabytes. For example, the value "10KB" will be interpreted
     as 10240.
     */
    public
    void setMaxFileSize(String value) {
        maxFileSize = OptionConverter.toFileSize(value, maxFileSize + 1);
    }

    protected
    void setQWForFiles(Writer writer) {
        this.qw = new CountingQuietWriter(writer, errorHandler);
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
            if (size >= maxFileSize && size >= nextRollover) {
                rollOver();
            }
        }
    }
}
