package uranoscopidae.teambuilder.utils;

import uranoscopidae.teambuilder.app.TeamBuilderApp;

import java.io.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.*;
import java.util.*;

public abstract class DataHolder implements Serializable
{

    private final List<Field> fields;
    private final List<MethodHandle> pointers;
    private final TeamBuilderApp app;

    public DataHolder(TeamBuilderApp app)
    {
        this.app = app;
        fields = new LinkedList<>();
        pointers = new LinkedList<>();
        Field[] allFields = getClass().getDeclaredFields();
        for(Field f : allFields)
        {
            if(f.isAnnotationPresent(SerializableField.class))
            {
                checkType(f, f.getType());
            }
        }
        Collections.sort(fields, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        fields.forEach(f -> {
            String pointer = f.getAnnotation(SerializableField.class).value();
            try
            {
                if(pointer != null && !pointer.isEmpty())
                {
                    pointers.add(getHandle(f, f.getType(), lookup, pointer));
                }
                else
                {
                    pointers.add(null);
                }
            }
            catch (IllegalAccessException | NoSuchMethodException e)
            {
                e.printStackTrace();
            }
        });
    }

    private final MethodHandle getHandle(Field f, Class<?> type, MethodHandles.Lookup lookup, String pointer) throws NoSuchMethodException, IllegalAccessException
    {
        MethodHandle handle;
        if(type.isArray())
        {
            Class<?> compType = f.getType().getComponentType();
            return getHandle(f, compType, lookup, pointer);
        }
        handle = lookup.findVirtual(type, pointer, MethodType.methodType(String.class));
        System.out.println("!!! "+pointer);
        return handle;
    }

    private void checkType(Field f, Class<?> type)
    {
        if(type == String.class || !f.getAnnotation(SerializableField.class).value().isEmpty())
        {
            f.setAccessible(true);
            fields.add(f);
            return; // we are writing a string value, it is indeed serializable
        }
        if(Serializable.class.isAssignableFrom(type) || type.isEnum() || type.isPrimitive())
        {
            f.setAccessible(true);
            fields.add(f);
        }
        else if(type.isArray())
        {
            Class<?> arrayType = type.getComponentType();
            checkType(f, arrayType);
            f.setAccessible(true);
            fields.add(f);
        }
        else
        {
            throw new IllegalArgumentException(f.getType()+" is not serializable! (in "+getClass().getCanonicalName()+")");
        }
    }

    public void writeTo(OutputStream out) throws IOException
    {
        DataOutputStream dataOut = new DataOutputStream(out);
        dataOut.writeInt(0xCAFEBABE);
        dataOut.writeUTF(getClass().getCanonicalName());
        for (int i = 0; i < fields.size(); i++)
        {
            Field f = fields.get(i);
            MethodHandle pointer = pointers.get(i);
            try
            {
                Object val = f.get(this);
                writeValue(dataOut, f.getType(), val, pointer);
            }
            catch (Throwable throwable)
            {
                throwable.printStackTrace();
            }
        }
        dataOut.flush();
    }

    private void writeValue(DataOutputStream out, Class<?> type, Object val, MethodHandle pointer) throws Throwable
    {
        out.writeBoolean(val == null);
        if(val == null)
            return;
        if(type.isArray())
        {
            int l = Array.getLength(val);
            out.writeInt(l);
            Class<?> compType = type.getComponentType();
            for (int i = 0; i < l; i++)
            {
                writeValue(out, compType, Array.get(val, i), pointer);
            }
        }
        else if(type.isEnum())
        {
            out.writeUTF(((Enum)val).name());
        }
        else if(type == String.class)
        {
            out.writeUTF((String) val);
        }
        else if(type.isPrimitive())
        {
            writePrimitive(out, type, val);
        }
        else if(pointer != null)
        {
            String result = (String) pointer.invoke(val);
            out.writeUTF(result);
        }
        else
        {
            Serializable serializable = (Serializable) val;
            serializable.writeTo(out);
        }
    }

    private void writePrimitive(DataOutputStream out, Class<?> type, Object val) throws IOException
    {
        if(type == boolean.class)
        {
            out.writeBoolean((Boolean) val);
        }
        else if(type == byte.class)
        {
            out.writeByte((Byte) val);
        }
        else if(type == char.class)
        {
            out.writeChar((Character) val);
        }
        else if(type == double.class)
        {
            out.writeDouble((Double) val);
        }
        else if(type == float.class)
        {
            out.writeFloat((Float) val);
        }
        else if(type == int.class)
        {
            out.writeInt((Integer) val);
        }
        else if(type == long.class)
        {
            out.writeLong((Long) val);
        }
        else if(type == short.class)
        {
            out.writeShort((Short) val);
        }
    }

    public void readFrom(InputStream in) throws IOException
    {
        DataInputStream dataIn = new DataInputStream(in);
        int magicNumber = dataIn.readInt();
        if(magicNumber != 0xCAFEBABE)
        {
            throw new IOException("Invalid magic number: "+magicNumber+" (expected: "+0xCAFEBABE+")");
        }
        String id = dataIn.readUTF();
        if(!id.equals(getClass().getCanonicalName()))
            throw new UnsupportedOperationException("Invalid id: "+id+" (expected "+getClass().getCanonicalName()+")");
        for (int i = 0; i < fields.size(); i++)
        {
            Field f = fields.get(i);
            boolean hasPointer = pointers.get(i) != null;
            try
            {
                f.set(this, readField(dataIn, f, f.getType(), hasPointer));
            }
            catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e)
            {
                e.printStackTrace();
            }
        }
    }

    private Object readField(DataInputStream dataIn, Field f, Class<?> type, boolean hasPointer) throws NoSuchMethodException, IOException, InvocationTargetException, IllegalAccessException, InstantiationException
    {
        if(dataIn.readBoolean())
        {
            return null;
        }
        if (type.isEnum())
        {
            String value = dataIn.readUTF();
            Class<? extends Enum> enumType = (Class<? extends Enum>) f.getType();
            return Enum.valueOf(enumType, value);
        }
        else if(type.isArray())
        {
            int length = dataIn.readInt();
            Class<?> compType = type.getComponentType();
            Object array = Array.newInstance(compType, length);
            List l = new LinkedList<>();
            for (int i = 0; i < length; i++)
            {
                l.add(readField(dataIn, f, compType, hasPointer));
            }
            return l.toArray((Object[]) array);
        }
        else if(type == String.class)
        {
            return dataIn.readUTF();
        }
        else if(type.isPrimitive())
        {
            return readPrimitive(dataIn, type);
        }
        else if(hasPointer)
        {
            String name = type.getSimpleName();
            String toCall = "get"+name;
            Method m = app.getClass().getDeclaredMethod(toCall, String.class);
            String value = dataIn.readUTF();
            return m.invoke(app, value);
        }
        else
        {
            Constructor<Serializable> cons = (Constructor<Serializable>) type.getDeclaredConstructor();
            cons.setAccessible(true);
            Serializable newInstance = cons.newInstance();
            newInstance.readFrom(dataIn);
            return newInstance;
        }
    }

    private Object readPrimitive(DataInputStream dataIn, Class<?> type) throws IOException
    {
        if(type == boolean.class)
        {
            return dataIn.readBoolean();
        }
        else if(type == byte.class)
        {
            return dataIn.readByte();
        }
        else if(type == char.class)
        {
            return dataIn.readChar();
        }
        else if(type == double.class)
        {
            return dataIn.readDouble();
        }
        else if(type == float.class)
        {
            return dataIn.readFloat();
        }
        else if(type == int.class)
        {
            return dataIn.readInt();
        }
        else if(type == long.class)
        {
            return dataIn.readLong();
        }
        else if(type == short.class)
        {
            return dataIn.readShort();
        }
        throw new IllegalArgumentException("Unknown primitive type: "+type.getCanonicalName());
    }

    @Override
    public void readFrom(DataInputStream in) throws IOException
    {
        readFrom((InputStream) in);
    }

    @Override
    public void writeTo(DataOutputStream out) throws IOException
    {
        writeTo((OutputStream)out);
    }
}
