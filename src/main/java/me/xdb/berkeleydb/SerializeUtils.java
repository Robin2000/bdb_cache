package me.xdb.berkeleydb;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class SerializeUtils {
	private static final Logger logger = Logger.getLogger(SerializeUtils.class.toString()); 

    private static void close(ObjectOutputStream objectOutputStream, ByteArrayOutputStream byteArrayOutputStream) {
        try {
            if (byteArrayOutputStream != null) {
                byteArrayOutputStream.close();
            }
        } catch (Exception e) {
        	if (logger.isLoggable(Level.WARNING)) {
                logger.warning("关闭IO资源异常[" + e.getMessage() + "]");
            }
        }
        try {
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
        } catch (Exception e) {
        	if (logger.isLoggable(Level.WARNING)) {
                logger.warning("关闭IO资源异常[" + e.getMessage() + "]");
            }
        }
    }

    private static void close(ObjectInputStream objectInputStream, ByteArrayInputStream byteArrayInputStream) {
        try {
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (byteArrayInputStream != null) {
                byteArrayInputStream.close();
            }
        } catch (Exception e) {
        	if (logger.isLoggable(Level.WARNING)) {
                logger.warning("关闭IO资源异常[" + e.getMessage() + "]");
            }
        }
    }

    public static byte[] serialize(Object object) {

        if (object == null) {
            return new byte[0];
        }
		
		/*if (object instanceof String) {错误，作为value的字符串也不可直接这样处理
			return object.toString().getBytes();
		}*/

        if (!(object instanceof Serializable)) {
            throw new IllegalArgumentException(SerializeUtils.class.getSimpleName() + " requires a Serializable payload " +
                    "but received an object of type [" + object.getClass().getName() + "]");
        }

        ObjectOutputStream objectOutputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return bytes;
        } catch (Exception e) {
        	if (logger.isLoggable(Level.WARNING)) {
                logger.warning("序列化对象异常[" + e.getMessage() + "]");
            }
        } finally {
            close(objectOutputStream, byteArrayOutputStream);
        }
        return null;
    }

    /**
     * 反序列化
     *
     * @param bytes
     * @return
     */
    public static Object deserialize(byte[] bytes) {

        Object result = null;

        if (isEmpty(bytes)) {
            return null;
        }

        try {
            ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(byteStream);
                try {
                    result = objectInputStream.readObject();
                } catch (ClassNotFoundException ex) {
                    throw new Exception("Failed to deserialize object type", ex);
                }
            } catch (Throwable ex) {
                throw new Exception("Failed to deserialize", ex);
            }
        } catch (Exception e) {
        	if (logger.isLoggable(Level.WARNING)) {
                logger.warning("Failed to deserialize");
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> T unserialize(byte[] bytes) throws Exception {
        if (isEmpty(bytes)) {
            return null;
        }
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return (T) objectInputStream.readObject();
        } finally {
            close(objectInputStream, byteArrayInputStream);
        }
    }

    public static boolean isEmpty(byte[] data) {
        return (data == null || data.length == 0);
    }

}
