package kafka;

import com.github.javafaker.Faker;
import java.util.Random;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer;
import com.example.CardData;
import java.util.Properties;

public class SendKafkaProto {
    public static void main(String[] args) {
        // Setup Producer Properties
        String bootstrapServers = "localhost:29092";
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", bootstrapServers);
        properties.setProperty("schema.registry.url", "http://localhost:8085");
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", KafkaProtobufSerializer.class.getName());

        KafkaProducer<String, CardData.CreditCard> producer = new KafkaProducer<>(properties);
        // Specify Topic Name
        String topic = "protos_topic_cards";

        // Loop to Produce Fake Data
        for (int i = 0; i < 15; i++) {
            // creating Random object
            Random rd = new Random();
            Faker faker = new Faker();
            String name = faker.name().fullName();
            String countryCode = faker.address().countryCode();
            String cardNumber = faker.business().creditCardNumber();
            Integer typeValue = rd.nextInt(3);
            String currencyCode = faker.country().currencyCode();

            // Serializing to Protobuf based on CreditCard.proto Schema
            CardData.CreditCard cardData = CardData.CreditCard.newBuilder()
                    .setName(name)
                    .setCountry(countryCode)
                    .setCurrency(currencyCode)
                    .setTypeValue(typeValue)
                    .setBlocked(false)
                    .setCardNumber(cardNumber)
                    .build();

            ProducerRecord<String, CardData.CreditCard> record = new ProducerRecord<>(topic, "Credit Card", cardData);
            // Send to Producer
            producer.send(record);
        }
        producer.flush();
        producer.close();
        // Log success message
        System.out.println("Sent Data Successfully");
    }
}
