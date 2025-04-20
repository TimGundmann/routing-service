package dk.gundmann.routing.visit;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Duration;

public class DurationSerializer extends JsonSerializer<Duration> {
    @Override
    public void serialize(Duration value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        long hours = value.toHours();
        long minutes = value.minusHours(hours).toMinutes();
        gen.writeString(String.format("%02d:%02d", hours, minutes));
    }
}