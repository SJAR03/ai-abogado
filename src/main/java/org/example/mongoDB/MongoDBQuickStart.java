import static com.mongodb.client.model.Filters.eq;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDBQuickStart {

    public static void main(String[] args) {
        // Reemplaza el marcador de posición con el string de conexión de tu despliegue de MongoDB
        String uri = "<connection string uri>";

        // Try-with-resources to ensure the MongoClient is closed after use
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("sample_mflix");
            MongoCollection<Document> collection = database.getCollection("movies");
            Document doc = collection.find(eq("title", "Back to the Future")).first();

            if (doc != null) {
                System.out.println(doc.toJson());
            } else {
                System.out.println("No matching documents found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
