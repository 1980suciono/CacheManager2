package com.indolibs.libs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class CacheManager2 {
	private static final long DEFAULT_MAX_SIZE = 83886080; // 80 MB
    private volatile long maxSize = DEFAULT_MAX_SIZE;
    private File cacheDir;

    public CacheManager2(File cacheDirectory) {
        this.cacheDir = cacheDirectory;
        if (!this.cacheDir.exists()) {
            this.cacheDir.mkdirs();
        }
    }
	
	public long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(long maxSizeBytes) {
        if (maxSizeBytes <= 0) {
            throw new IllegalArgumentException("maxSizeBytes must be greater than 0");
        }
        this.maxSize = maxSizeBytes;
    }

	public CacheManager2 toDefaultSizeBytes() {
        this.maxSize = DEFAULT_MAX_SIZE;
		return this;
    }

    public void cacheData(byte[] bArr, String str) {
        if (cacheDir == null) return;
        long length = ((long) bArr.length) + getDirSize(cacheDir);
        if (length > maxSize) {
            cleanDir(cacheDir, length - maxSize);
        }

		File targetFile = new File(cacheDir, str);
		try (FileOutputStream fileOutputStream = new FileOutputStream(targetFile)) {
			fileOutputStream.write(bArr);
			fileOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    }

    public void delCacheFile(String str) {
        if (cacheDir == null) return;
        File file = new File(cacheDir, str);
        if (file.exists()) {
            file.delete();
        }
    }

    public byte[] retrieveData(String str) {
        if (cacheDir == null) return null;
        File file = new File(cacheDir, str);
        return fileToBytes(file);
    }
	
	public byte[] fileToBytes(String filePath) {
		File file = new File(filePath);
        return fileToBytes(file);
	}
	
	public byte[] fileToBytes(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
			return null;
		}
		long len = file.length();
		if (len > Integer.MAX_VALUE) {
			return null;
		}
        byte[] bArr = new byte[(int) len];
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            int offset = 0;
			while (offset < bArr.length) {
				int bytesRead = fileInputStream.read(bArr, offset, bArr.length - offset);
				if (bytesRead == -1) {
					break;
				}
				offset += bytesRead;
			}
        } catch (Exception e) {
			e.printStackTrace();
			return null;
		}
        return bArr;
	}

    private static void cleanDir(File file, long j) {
        long length = 0;
        File[] files = file.listFiles();
        if (files != null) {
            for (File file2 : files) {
                length += file2.length();
                file2.delete();
                if (length >= j) {
                    return;
                }
            }
        }
    }

    private static long getDirSize(File file) {
        long length = 0;
        File[] files = file.listFiles();
        if (files != null) {
            for (File file2 : files) {
                if (file2.isFile()) {
                    length += file2.length();
                }
            }
        }
        return length;
    }

    public void clearCaches() throws Exception {
        if (cacheDir != null && cacheDir.exists() && cacheDir.isDirectory()) {
            File[] fileArrListFiles = cacheDir.listFiles();
            if (fileArrListFiles != null && fileArrListFiles.length > 0) {
                for (File file : fileArrListFiles) {
                    file.delete();
                }
            }
        }
    }

    public void cacheImage(byte[] bArr, String str) {
        cacheData(bArr, str + "image");
    }

    public byte[] retrieveImageBytes(String str) {
        return retrieveData(str + "image");
    }
}