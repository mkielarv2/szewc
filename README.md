You can find .apk file in a releases tab

# Najbliższy sprint:
 - Migracja na kotlin + navigation components + refactor
 - Utworzenie serwera + repo
 - Połączenia socketowe
 - Działające 1v1 (przez kod) z obsługą wielu gier na raz (id + nick użytkownika)

# Flow:


# Założenia:
 - Model 1v1 (z możliwością powiększenia)
 - Lobby + kod + link
 - Użytkownik ma generowane ID + nick?
 - Lokalne statystyki (win/loss, ilość gier)
 - Firebase (możliwy hosting)
 - Biała flaga

# Menu:
 - Znajdź grę
 - Zaproś/Utwórz (link/kod)
 - Zasady w skrócie
 - Statystyki
 - Wyjdź z gry

# TODO:
 - Migracja na kotlin
 - Projekt UI
 - Projekt Menu
 - Projekt Flow w aplikacji
 - Donate button dla emila na mikro

# Protokół komunikacyjny:
REST (menu/lobby)
Sockety (gra)

JSON (wszystko)

3x3
[0,0,0,1,0,0,0,0,0]
[0,0,0,0,2,0,0,0,0]
