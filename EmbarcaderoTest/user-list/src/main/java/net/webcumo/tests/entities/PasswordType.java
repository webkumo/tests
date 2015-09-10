package net.webcumo.tests.entities;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StringType;
import org.hibernate.usertype.UserType;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.security.Key;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PasswordType implements UserType {
    private static final StringType TYPE = StringType.INSTANCE;
    public static final String ENC_ALGORITHM = "AES";
    private final byte[] passwordKey = "some_password_ke".getBytes();

    @Override
    public int[] sqlTypes() {
        return new int[] {TYPE.sqlType()};
    }

    @Override
    public Class returnedClass() {
        return String.class;
    }

    @Override
    public boolean equals(Object o, Object o1) throws HibernateException {
        return o == null && o1 == null || o != null && o.equals(o1);
    }

    @Override
    public int hashCode(Object o) throws HibernateException {
        if (o != null) {
            return o.hashCode();
        }
        return 0;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
            throws HibernateException, SQLException {
        return decrypt((String)TYPE.nullSafeGet(rs, names[0], session, owner));
    }

    @Override
    public void nullSafeSet(PreparedStatement ps, Object value, int i, SessionImplementor session) throws HibernateException, SQLException {
        TYPE.nullSafeSet(ps, encrypt((String)value), i, session);
    }

    @Override
    public Object deepCopy(Object o) throws HibernateException {
        return o;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object o) throws HibernateException {
        return (String) o;
    }

    @Override
    public Object assemble(Serializable serializable, Object o) throws HibernateException {
        return serializable;
    }

    @Override
    public Object replace(Object o, Object o1, Object o2) throws HibernateException {
        return o;
    }

    private String encrypt(String Data) {
        try {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ENC_ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(Data.getBytes());
            return new BASE64Encoder().encode(encVal);
        } catch (Exception e) {
            //TODO log
            e.printStackTrace();
            return null;
        }
    }

    private String decrypt(String encryptedData) {
        try {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ENC_ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = new BASE64Decoder().decodeBuffer(encryptedData);
            byte[] decValue = c.doFinal(decoded);
            return new String(decValue);
        } catch (Exception e) {
            //TODO log
            e.printStackTrace();
            return null;
        }
    }

    private Key generateKey() {
        return new SecretKeySpec(passwordKey, ENC_ALGORITHM);
    }}
