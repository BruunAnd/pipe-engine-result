package dk.anderslangballe.trees;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

@JsonSerialize(using = TypeSerializer.class)
public enum NodeType {
    JOIN("\u22C8"),
    LEFT_JOIN("\u27d5"),
    PROJECTION("\u03C0"),
    FILTER("\u03C3"),
    UNION("\u222A"),
    EMPTY("\u2205");

    private String value;

    NodeType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}


class TypeSerializer extends StdSerializer<NodeType> {
    public TypeSerializer() {
        super(NodeType.class);
    }

    public TypeSerializer(Class t) {
        super(t);
    }

    public void serialize(NodeType type, JsonGenerator generator, SerializerProvider provider)  throws IOException {
        generator.writeString(type.toString());
    }
}