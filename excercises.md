# JPA adatbázis-programozási gyakorlat - Posts

Ebben a feladatban egy közösségi oldal vázlatos felépítésén keresztül lehet gyakorolni az adatbázis műveleteket
a JPA keretrendszer használatával. Törekedj a háromrétegű alkalmazás-architektúra kialakítására!
A feladatok megoldását ennek a repository-nak a `solutions' mappájában találod. Ahol tudsz, használj AssertJ-t a teszteléshez!
Jó gyakorlást!

## Előkészületek

Hozz létre egy új adatbázis sémát `socialnetwork` névvel! A gyakorlat során itt hozd létre a táblákat,
amikbe majd az adatok kerülnek. Használd a JPA automatikus sémagenerálását! Válassz egy tetszőleges felhasználónevet és jelszót
az adatbázis eléréséhez! Biztosítsd neki a szükséges jogokat!

Hozz létre egy új Maven projektet az IDEA-ban! Az `artifactId` legyen `jpa-socialnetwork-practice`! Hozd létre a megfelelő útvonalon a 
`persistence.xml` állományt, amiben a JPA működéséhez szükséges *persistence unit*-ot!

A `socialnetwork` csomagban dolgozz!

## User entitás

Az oldal alapvető egysége a felhasználó. Hozd létre a `User` entitást, ami rendelkezik egy `Long id` egyedi azonosítóval,
egy `String userName` felhasználónévvel, és `String password` jelszóval! Legyen egy `Category category` nevű attribútuma, ami
egy enum, és a következő értékeket veheti fel: `FREE`, `PREMIUM`, `VIP`! Ügyelj rá, hogy szövegesen kerüljön mentésre! Konfiguráld
az oszlopok nevét konvenció szerint! A felhasználónévnek egyedinek kell lennie az adatbázisban.
Hozd létre a megfelelő konstruktorokat, valamint getter és setter metódusokat!

### Azonosítógenerálás

Az azonosítók táblagenerátorral kerüljenek kiosztásra minden további entitás esetén is, egyetlen `id_table` nevű táblában! Ehhez
konfigurálj egy táblagenerátort a következőképpen:
* a táblák neve a `table_name` oszlopba kerüljenek (elsődleges kulcs)
* az elsődleges kulcs a tábla neve legyen
* a következő kiosztott érték az `id_value` oszlopba kerüljön
* az azonosítókat tízesével kérje le a keretrendszer


### Alapvető műveletek

Hozd létre a `UserDao` osztályt, ami alapvető CRUD műveleteket tartalmazza! Legyen benne egy `void saveUser(User user)` metódus mentéshez,
egy `void deleteUserById(long userId)` törléshez, egy `User findUserById(long id)` olvasáshoz, és egy `boolean updateUser(long id, String newPassword)`
a jelszó módosításhoz! Utóbbi csak akkor végezzen frissítést, ha az új jelszó eltér! Ellenkező esetben `false` értékkel térjen vissza!
Legyen egy túltöltött változata a kategória módosításához is! Ahol lehet, használj referenciákat a műveletek során!

### Személyes adatok (opcionális)

A felhasználók személyes adatait a `PersonalData` beágyazható osztály tartalmazza! Ehhez hozd létre a `PersonalData personalData` attribútumot! 
Az osztály a `String realName`, `LocalDate dateOfBirth`, és `String city` adatokat tárolja! Próbáld meg kiszervezni egy másodlagos táblába, aminek a
neve `personal_data` legyen. A felhasználó azonosítóját a `user_id` oszlop tartalmazza!

Segítség [ebben a leckében](https://e-learning.training360.com/courses/take/adatbazis-programozas-jpa-technologiaval/lessons/10769298-beagyazott-objektumok-es-masodlagos-tabla).

### Regisztráció dátuma

A regisztráció dátumát a `LocalDate registrationDate` attribútum tárolja! Állítsd be, hogy a felhasználó mentése során automatikusan megkapja
az aktuális dátumot és azzal együtt kerüljön perzisztálásra!

## Posztok és kommentek (one-to-many)

Minden felhasználó bejegyzéseket tud közzétenni, amik a `Set<Post> posts` halmazba kerülnek. Egy posztnak rendelkeznie kell egy `Long id`
egyedi azonosítóval, a létrehozás dátumával (`LocalDateTime postDate`), egy enum `Content content` típussal, ami a `TEXT`, `IMAGE`, `VIDEO` példányokat
veheti fel! Figyelj az enum szöveges mentésére! Elég csak a tartalmat beállítani konstruktorban. A dátum perzisztálás előtt legyen beállítva automatikusan!

Konfigurálj kétirányú kapcsolatot a `User` (inverse side) és a `Post` (owner side) között! Legyen a `User` entitásnak egy `void addPost(Post post)`
metódusa, ami létre is hozza ezt!

### Bejegyzések kezelése

Mivel az új felhasználóknak nincsenek posztjaik, ezért kaszkádolt mentést felesleges beállítani. Helyette a `PostDao` osztályban írj egy olyan
`void savePost(long userId, Post post)` metódust, ami a felhasználó referenciáján át elmenti az adott bejegyzést az adatbázisba!

Mivel a posztok bírnak a külső kulccsal, ezért egy felhasználó törlésére tett kísérlet kivételt eredményezne, mert azok nem létező felhasználóra mutatnának.
Ezért állíts be kaszkádolt törlést, hogy ezt elkerüld!

Legyen egy `List<Post> listAllPostsOfUser(long userId)` metódus, ami kilistázza időrendi sorrendben egy felhasználó azonosítója alapján a bejegyzéseit! Próbáld meg
úgy implementálni, hogy nem töltöd be a felhasználót a perzisztencia kontextusba!

Hozz létre egy `List<Post> listPostsOfUser(long userId, Predicate<Post> predicate)` metódust, ami tetszőleges feltétel alapján szűrt bejegyzéseket képes
visszaadni egy listában! Az eredményeket streamként kérd le!

### Kommentek létrehozása

A felhasználók megjegyzéseket írhatnak az egyes bejegyzések alá, amiket a `Comment` entitás példányai reprezentálnak.

A `Comment` entitás a következő attribútumokkal rendelkezzen:
* `Long id` egyedi azonosító
* `LocalDateTime commentDate` dátum
* `String commentText` szöveges tartalom
* `User user` felhasználó
* `Post post` bejegyzés

A konstruktorban csak a szöveges tartalmat kell megadni. A dátum a perzisztálás során automatikusan kerüljön beállításra! 
Konfiguráld megfelelően az entitást - állítsd be a táblagenerátort és ügyelj az attribútumok neveire! A komment szövegének hossza 8192 karakter lehet!

Alakíts ki kétirányú one-to-many kapcsolatokat! A `Post` (inverse side) `Set<Comment> comments` tartalmazza a kommenteket, amire a `Comment` (owner side)
`Post post` attibútuma mutat. Ne feledd, hogy a kommentek egyszerre a felhasználókhoz is tartoznak, ezért hozz létre a `User` (inverse side) entitásban egy
`Set<Comment> comments` attribútumot, amire a `Comment`-ben (owner side) a `User user` (inverse side) mutat! A kapcsolatok létrehozása a `Post` entitás feladata.
Az ebben lévő `void addComment(User user, Comment comment)` metódus végzi ezt el.

### Kommentek kezelése

A kommentekkel kapcsolatos adatbázis-műveletek a `CommentDao` osztályban kapnak helyet. Készíts egy `void saveComment(long userId, long postId, Comment comment)`
metódust az új hozzászólások mentéséhez! Használd bátran a referenciákat!

Az olvasást több irányból is meg lehet közelíteni. Írj egy `List<Comment> listCommentsOfUser(long userId)` metódust, amiben egy `NamedQuery`-t használsz a
felhasználó által írt kommentek listázásához! Készíts egy olyan `List<Comment> listCommentsUnderPostsByUser(long ownerId, long writerId)` metódust,
ami viszont egy felhasználó összes bejegyzéséhez fűzött kommenteket közül listázza ki azokat, amiket egy adott felhasználó írt!

Legyen egy metódus, ami lapozást használ az összes komment lekérésekor (`List<Comment> listCommentsWithPaging(int firstIndex, int max)`)!

#### Az orphanRemoval használata

Az `ophanRemoval` az árván maradt objektumok automatikus törlését jelenti. Ez akkor történhet meg, ha egy gyermek objektumot eltávolítunk egy kollekcióból
a `remove()` metódussal. Így ha az `orphanRemoval = true` beállítás meg van adva a szülőben, akkor ez a rekordok szintjén is megjelenik anélkül, hogy az
`EntityManager` `remove()` metódusát használni kellene. Ez akkor is működik, ha esetleg a gyermek objektum maga is kollekciót tartalmaz. Természetesen a
külső kulcsok megkötését itt sem lehet megsérteni, ezért azokban is meg kell adni az `orphanRemoval = true` beállítást.

**Vigyázz, a funkció bugos (hybernate-entitymanager 5.6.14.Final)! A `CascadeType.PERSIST` együttes megadása nélkül nem működik.**

Írj egy metódust a `PostDao` osztályban egy felhasználó összes bejegyzésének törlésére (`void deleteAllPostsByUserId(long userId)`)! Használd ki az
`orphanRemoval`-t! Próbáld ki kommentek hozzáadásával is!

#### Projection query

Készíts egy egyszerű `PostData` objektumot, ami a posztoló felhasználónevét (`String userName`), egy bejegyzés tartalmát(`Content content`), és
a hozzá fűzött kommentek számát (`int commentCount`) tartalmazza!

Írj egy `List<PostData> listPostDataByUserId(long userId)` metódust a `PostDao` osztályba, ami összegyűjti egy felhasználó posztjainak az adatait DTO objektumokba!

## Csoportok (many-to-many)

A felhasználók beléphetnek csoportokba is az oldalon: egy felhasználó többhöz is tartozhat, míg egy csoportban több felhasználó is lehet.

Hozd létre a `Group` entitást a következő attribútumokkal:

* `Long id` egyedi azonosító
* `String name;` név
* `String description` csoportleírás
* `Set<User> users` csoporttagok
* `boolean privateGroup` publikus/privát csoport

Konfiguráld az entitást a megfelelő attribútumokkal! A név legyen egyedi, 

Alakíts ki kétirányú many-to-many kapcsolatot a `User` (owner side) és a `Group` (inverse side) között! Ehhez a `User` entitásban is vedd fel a
megfelelő attribútumot! A kapcsolótábla neve legyen `users_to_groups`, az oszlopai sorban `user_id` és `group_id` legyenek! A kapcsolatot a `Group`
példányok `void addUser(User user)` metódusa hozza létre!

### Csoportok kezelése

Készítsd el a `GroupDao` osztályban a `void saveGroup(Group group)`, a `void addUserToGroup(long userId, long groupId)`, és a `List<User> listGroupMembers(long groupId)`
nevű metódusokat! Arra az esetre is írj egyet, ha egy felhasználó ki akar lépni egy csoportból (`void removeUserFromGroup(long userId, long groupId)`)!

## Ismerősök hozzáadása (many-to-many)

Egy felhasználónak lehetnek ismerősei is az oldalon. Lehetőséged van kialakítani egy entitáson belül is many-to-many kapcsolatot, ha egyszerűen mind az owner side, mind az
inverse side beállításait egyetlen attribútumon végzed el.

Készíts egy `Set<User> friends` attribútumot a `User` entitáson belül! Konfigurálj many-to-many kapcsolatot, és hozd létre a `void addFriend(User user)` metódust!

A `UserDao` osztályban legyen egy `void saveFriendship(long userId1, long userId2)`, egy `List<User> listFriendsOfUser(long userId)`, és egy `void removeFriendship(long userId1, long userId2)` metódus!

## Fetch és entitás gráfok

A kapcsolatok betöltése vagy FetchType.EAGER vagy FetchType.LAZY típusú alapértelmezetten. A kettő közti finomhangolást entitás gráfokkal lehet megoldani
ahelyett, hogy a fetch-elést a JPQL utasításba égetnéd be. Emellett egy előre megírt gráfot több lekérdezéshez is használhatsz.

## Service réteg kialakítása

(talán később)
* SHA-256
* PREMIUM, VIP funkciók
* Mockito
* nemtom
