//
// JODConverter - Java OpenDocument Converter
// Copyright 2004-2011 Mirko Nasato and contributors
//
// JODConverter is free software: you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation, either version 3 of
// the License, or (at your option) any later version.
//
// JODConverter is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General
// Public License along with JODConverter.  If not, see
// <http://www.gnu.org/licenses/>.
//
package bboss.org.artofsolving.jodconverter.office;

import java.net.ConnectException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import bboss.org.artofsolving.jodconverter.util.PlatformUtils;

import com.sun.star.frame.XDesktop;
import com.sun.star.lang.DisposedException;

public class ManagedOfficeProcess {

    public static final Integer EXIT_CODE_NEW_INSTALLATION = Integer.valueOf(81);

    private final ManagedOfficeProcessSettings settings;

    private final OfficeProcess process;
    private final OfficeConnection connection;

    private ExecutorService executor = Executors.newSingleThreadExecutor(new NamedThreadFactory("OfficeProcessThread"));

    private final Logger logger = Logger.getLogger(ManagedOfficeProcess.class);

    public ManagedOfficeProcess(ManagedOfficeProcessSettings settings) throws OfficeException {
        this.settings = settings;
        process = new OfficeProcess(settings.getOfficeHome(), settings.getUnoUrl(), settings.getRunAsArgs(),
                settings.getTemplateProfileDir(), settings.getWorkDir(), settings.getProcessManager());
        connection = new OfficeConnection(settings.getUnoUrl());
    }

    public OfficeConnection getConnection() {
        return connection;
    }

    public void startAndWait() throws OfficeException {
        Future<?> future = executor.submit(new Runnable() {
            public void run() {
                doStartProcessAndConnect();
            }
        });
        try {
            future.get();
        } catch (Exception exception) {
            throw new OfficeException("failed to start and connect", exception);
        }
    }

    public void stopAndWait() throws OfficeException {
        Future<?> future = executor.submit(new Runnable() {
            public void run() {
                doStopProcess();
            }
        });
        try {
            future.get();
        } catch (Exception exception) {
            throw new OfficeException("failed to start and connect", exception);
        }
    }

    public void restartAndWait() {
        Future<?> future = executor.submit(new Runnable() {
           public void run() {
               doStopProcess();
               doStartProcessAndConnect();
            } 
        });
        try {
            future.get();
        } catch (Exception exception) {
            throw new OfficeException("failed to restart", exception);
        }
    }

    public void restartDueToTaskTimeout() {
        executor.execute(new Runnable() {
           public void run() {
                doTerminateProcess();
                // will cause unexpected disconnection and subsequent restart
            } 
        });
    }

    public void restartDueToLostConnection() {
        executor.execute(new Runnable() {
            public void run() {
                try {
                    doEnsureProcessExited();
                    doStartProcessAndConnect();
                } catch (OfficeException officeException) {
                    logger.error( "could not restart process", officeException);
                }
            } 
         });
    }
    private static class OSFlag
    {
    	boolean iswindow = false;
    }
    private void doStartProcessAndConnect() throws OfficeException {
        try {
        	final OSFlag isWindow = new OSFlag();
        	if(PlatformUtils.isLinux())
        	{
        		try {
					process.start();
				} catch (Exception officeException) {
					logger.error( "Start libreoffice failed,Try connect an external soffice service.", officeException);
					new Retryable() {
		                protected void attempt() throws TemporaryException, Exception {
		                    try {
		                        connection.connect();
		                    } catch (ConnectException connectException) {
		                            throw new OfficeException("office process died with exit code ");
		                        
		                    }
		                }
		            }.execute(settings.getRetryInterval(), settings.getRetryTimeout());
		            return;
				}
        	}
        	else
        		isWindow.iswindow = true;
            new Retryable() {
                protected void attempt() throws TemporaryException, Exception {
                    try {
                        connection.connect();
                    } catch (ConnectException connectException) {
                        Integer exitCode = process.getExitCode();
                        if (exitCode == null) {
                            // process is running; retry later
                            throw new TemporaryException(connectException);
                        } else if (exitCode.equals(EXIT_CODE_NEW_INSTALLATION)) {
                            // restart and retry later
                            // see http://code.google.com/p/jodconverter/issues/detail?id=84
                            logger.warn( "office process died with exit code 81; restarting it");
                            if(!isWindow.iswindow)
                            {
                            	process.start(true);
                            	throw new TemporaryException(connectException);
                            }
                            else
                            {
                            	process.start();
                            	isWindow.iswindow = false;
                            	try {
									connection.connect();
								} catch (Exception e) {
									process.start(true);
									connection.connect();
								}
                            	
                            }
                            
                        } else {
                            throw new OfficeException("office process died with exit code " + exitCode);
                        }
                    }
                }
            }.execute(settings.getRetryInterval(), settings.getRetryTimeout());
        } catch (Exception exception) {
            throw new OfficeException("could not establish connection", exception);
        }
    }

    private void doStopProcess() {
        try {
            XDesktop desktop = OfficeUtils.cast(XDesktop.class, connection.getService(OfficeUtils.SERVICE_DESKTOP));
            desktop.terminate();
            
        } catch (DisposedException disposedException) {
            // expected
        } catch (Exception exception) {
            // in case we can't get hold of the desktop
            doTerminateProcess();
        }
        doEnsureProcessExited();
    }

    private void doEnsureProcessExited() throws OfficeException {
    	if(process == null )
    		return;
        try {
            int exitCode = process.getExitCode(settings.getRetryInterval(), settings.getRetryTimeout());
            logger.info("process exited with code " + exitCode);
        } catch (RetryTimeoutException retryTimeoutException) {
            doTerminateProcess();
        }
        process.deleteProfileDir();
    }

    private void doTerminateProcess() throws OfficeException {
        try {
        	if(process == null )
        		return;
            int exitCode = process.forciblyTerminate(settings.getRetryInterval(), settings.getRetryTimeout());
            logger.info("process forcibly terminated with code " + exitCode);
        } catch (Exception exception) {
            throw new OfficeException("could not terminate process", exception);
        }
    }

}
