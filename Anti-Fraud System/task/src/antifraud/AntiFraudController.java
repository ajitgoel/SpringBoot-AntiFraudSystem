package antifraud;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashMap;
import java.util.Map;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/antifraud")
public class AntiFraudController {
    @PostMapping("/transaction")
    public Map<String, String> transaction(@RequestBody Map<String, Long> transactionRequest) {
        HashMap<String, String> map = new HashMap<>();
        if (transactionRequest==null || transactionRequest.get("amount")== null|| transactionRequest.get("amount") <=0) {
            throw new ResponseStatusException(BAD_REQUEST);
        }
        if (transactionRequest.get("amount")>0 && transactionRequest.get("amount") <= 200) {
            map.put("result", "ALLOWED");
            return map;
        }
        if (transactionRequest.get("amount") > 200 && transactionRequest.get("amount") <= 1500) {
            map.put("result", "MANUAL_PROCESSING");
            return map;
        }
        else {
            map.put("result", "PROHIBITED");
            return map;
        }
    }
}
