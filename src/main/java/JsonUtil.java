import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.Reader;
import java.io.Writer;
import java.util.Optional;

/**
 * Created by admin on 5/7/2016.
 */
public class JsonUtil
{
    static private final ObjectMapper objectMapper = new ObjectMapper();

    static public <T> Optional<T> convertJsonToObject(String jsonStr, Class<T> outObjectType)
    {
        try
        {
            T obj = objectMapper.readValue(jsonStr, outObjectType);

            return Optional.of(obj);
        }
        catch (Exception expt)
        {
            return Optional.empty();
        }
    }

    static public <T> Optional<T> convertJsonToObject(String jsonStr, JavaType outObjectType)
    {
        try
        {
            T obj = objectMapper.readValue(jsonStr, outObjectType);

            return Optional.of(obj);
        }
        catch (Exception expt)
        {
            return Optional.empty();
        }
    }

    static public <T> Optional<T> convertJsonToObject(Reader reader, Class<T> outObjectType)
    {
        try
        {
            T obj = objectMapper.readValue(reader, outObjectType);

            return Optional.of(obj);
        }
        catch (Exception expt)
        {
            return Optional.empty();
        }
    }

    static public <T> boolean sendObjectAsJson(Writer writer, T obj)
    {
        try
        {
            objectMapper.writeValue(writer, obj);

            return true;
        }
        catch (Exception expt)
        {
            return false;
        }
    }

    static public <T> Optional<String> convertObjectToJson(T obj)
    {
        try
        {
            return Optional.of( objectMapper.writeValueAsString(obj) );
        }
        catch (Exception expt)
        {
            return Optional.empty();
        }
    }

    static public TypeFactory getTypeFactory()
    {
        return objectMapper.getTypeFactory();
    }

    static public ObjectMapper getObjectMapper()
    {
        return objectMapper;
    }
}
