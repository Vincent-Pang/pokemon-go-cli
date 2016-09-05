import POGOProtos.Enums.PokemonFamilyIdOuterClass;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.inventory.Inventories;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;

import java.util.*;
import java.util.stream.Collectors;

public class Main
{
    public static class Response
    {
        @Getter
        @Setter
        public List<Integer> pokemonIdList = new ArrayList<>();

        @Getter
        @Setter
        public Map<Integer, Integer> pokemonIdCandyMap = new HashMap<>();
    }


    public static void main(String[] args)
    {
        final OkHttpClient httpClient = new OkHttpClient();
        final PokemonGo pokemonGo = new PokemonGo(httpClient);

        if ( !login(pokemonGo, httpClient) )
        {
            System.out.println("Login fail");
            System.out.println("Exit Program");
            return;
        }

        final String data = getInfo(pokemonGo);

        System.out.println(data);
    }

    private static boolean login(final PokemonGo pokemonGo, final OkHttpClient httpClient)
    {
        final Scanner in = new Scanner(System.in);

        System.out.println("Google Account or PTC Account? (input google/ptc)");
        final String accountType = in.next();

        System.out.println();

        switch (accountType)
        {
            case "google":
                return loginWithGoogle(pokemonGo, httpClient);
            case "ptc":
                return loginWIthPTC(pokemonGo, httpClient);
            default:
                System.out.println("input either google or ptc");
                return false;
        }
    }

    private static boolean loginWithGoogle(final PokemonGo pokemonGo, final OkHttpClient httpClient)
    {
        System.out.println("Login using google...");

        final Scanner in = new Scanner(System.in);

        System.out.println("Please go to ");
        System.out.println(GoogleUserCredentialProvider.LOGIN_URL);
        System.out.println();
        System.out.println("Access Token?");
        final String accessToken = in.next();

        try
        {
            final GoogleUserCredentialProvider provider = new GoogleUserCredentialProvider(httpClient);

            provider.login(accessToken);

            pokemonGo.login(provider);
        }
        catch (final Exception expt)
        {
            System.out.println(expt.toString());
            return false;
        }

        return true;
    }

    private static boolean loginWIthPTC(final PokemonGo pokemonGo, final OkHttpClient httpClient)
    {
        System.out.println("Login using PTC...");

        final Scanner in = new Scanner(System.in);

        System.out.println("Username?");
        final String userName = in.next();

        System.out.println("Password?");
        final String password = in.next();

        try
        {
            pokemonGo.login(new PtcCredentialProvider(httpClient, userName, password));
        }
        catch (final Exception expt)
        {
            System.out.println(expt.toString());
            return false;
        }

        return true;
    }

    private static String getInfo(final PokemonGo pokemonGo)
    {
        final Inventories inventories = pokemonGo.getInventories();

        final Response resp = new Response();

        resp.pokemonIdList = inventories.getPokebank().getPokemons().stream()
                .map(v -> v.getPokemonId())
                .map(v -> v.getNumber())
                .collect(Collectors.toList());

        resp.pokemonIdCandyMap = Arrays.asList( PokemonFamilyIdOuterClass.PokemonFamilyId.values() ).stream()
                .filter(v -> v != PokemonFamilyIdOuterClass.PokemonFamilyId.UNRECOGNIZED)
                .filter(v -> v != PokemonFamilyIdOuterClass.PokemonFamilyId.FAMILY_UNSET)
                .collect(
                        Collectors.toMap(
                            PokemonFamilyIdOuterClass.PokemonFamilyId::getNumber
                            , v -> inventories.getCandyjar().getCandies(v) )
                    );

        return JsonUtil.convertObjectToJson(resp).orElse("");
    }
}
