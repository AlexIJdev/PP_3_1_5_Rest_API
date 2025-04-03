package PP_3_1_5_Rest_API;

import PP_3_1_5_Rest_API.model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootApplication
public class RestTemplateApplication {
    public static final String GET_USERS_URL = "http://94.198.50.185:7081/api/users";
    public static final String POST_USER_URL = "http://94.198.50.185:7081/api/users";
    public static final String PUT_USER_URL = "http://94.198.50.185:7081/api/users";
    public static final String DELETE_USER_URL = "http://94.198.50.185:7081/api/users/{id}";
    private static final RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        SpringApplication.run(RestTemplateApplication.class, args);

        StringBuilder stringBuilder = new StringBuilder();

        // получение cookies
        List<String> cookies = getUsers().getHeaders().get(HttpHeaders.SET_COOKIE);

        // получение шестизначного кода из запроса по добавлению нового пользователя
        stringBuilder.append(addUser(new User(Long.parseLong("3"), "Alexander", "Borovik", (byte) 28), cookies));

        // получение шестизначного кода из запроса по обновлению пользователя
        stringBuilder.append(updateUser(new User(Long.parseLong("3"), "Alexander", "Borovik", (byte) 28), cookies));

        // получение шестизначного кода из запроса по удалению пользователя
        stringBuilder.append(deleteUser(new User(Long.parseLong("3"), "Alexander", "Borovik", (byte) 28), cookies));

        System.out.println(stringBuilder);
    }

    public static ResponseEntity<User[]> getUsers() {
        // выполнение GET-запроса: здесь важен просто полученный результат как ответ, для дальнейшего получения cookies
        return restTemplate.getForEntity(GET_USERS_URL, User[].class);
    }

    public static String addUser(User user, List<String> cookies) {
        // создание объекта HttpEntity с новым пользователем и заголовками
        HttpEntity<User> requestEntity = getHttpEntity(user, cookies);

        // выполнение POST-запроса на добавление нового пользователя, используя полученные ранее cookies
        ResponseEntity<String> response = restTemplate.exchange(POST_USER_URL, HttpMethod.POST, requestEntity, String.class);

        // завершение метода в зависимости от результата запроса
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            return "Failed to add user: " + response.getStatusCode() + ".";
        }
    }

    public static String updateUser(User user, List<String> cookies) {
        // создание объекта HttpEntity с обновленным пользователем и заголовками
        HttpEntity<User> requestEntity = getHttpEntity(user, cookies);

        // выполнение PUT-запроса на обновление пользователя, используя полученные ранее cookies
        ResponseEntity<String> response = restTemplate.exchange(PUT_USER_URL, HttpMethod.PUT, requestEntity, String.class);

        // завершение метода в зависимости от результата запроса
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            return "Failed to add user: " + response.getStatusCode() + ".";
        }
    }

    public static String deleteUser(User user, List<String> cookies) {
        // создание объекта HttpEntity с удаляемым пользователем и заголовками
        HttpEntity<User> requestEntity = new HttpEntity<>(user, getHeaders(cookies));

        // выполнение POST-запроса на удаление пользователя, используя полученные ранее cookies
        ResponseEntity<String> response = restTemplate.exchange(DELETE_USER_URL, HttpMethod.DELETE, requestEntity, String.class, user.getId());

        // завершение метода в зависимости от результата запроса
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            return "Failed to add user: " + response.getStatusCode() + ".";
        }
    }

    private static HttpHeaders getHeaders(List<String> cookies) {
        HttpHeaders headers = new HttpHeaders();

        // проверка cookies и добавление их в заголовки
        if (cookies != null && !cookies.isEmpty()) {
            String cookieHeader = String.join("; ", cookies);
            headers.add("Cookie", cookieHeader);
        }
        return headers;
    }

    private static HttpEntity<User> getHttpEntity(User user, List<String> cookies) {
        return new HttpEntity<>(user, getHeaders(cookies));
    }
}
