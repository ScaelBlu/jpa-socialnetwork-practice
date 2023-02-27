# JPA adatbázis-programozási gyakorlat - Social network

Ebben a projektben egy közösségi oldal sémáján keresztül lehet gyakorolni az adatbázis műveleteket JPA-val.
Használd a projektet szabadon egy homokozóként, amiben a JPA működését megismerheted! Egészítsd ki a saját metódusaiddal!
A projekt célja elsősorban segíteni a JPA működésének megértését, betanulni a használatát, és ösztönözni az egyéni tanulást, nem
pedig valós, gyakorlatban alkalmazott megoldások bemutatása. A repóban lévő információk és implementációk helyessége nem garantált -
saját felelősségedre használd! Feltöltéskor a tesztek lefutottak, de ennek ellenére is tartalmazhat hibákat. A feladatok megoldását
a repository `src` mappájában találod. Mind a feladatok, mind a megoldások a saját munkám eredményei. 
Törekedj a clean code elvek betartására! Írj teszteket is - ha szeretnéd, írd meg őket előre. Használd bátran az AssertJ-t is!

Jó gyakorlást!

## Előkészületek

Hozz létre egy új adatbázis sémát `socialnetwork` névvel! Ebben legyenek a generált táblák. Használd ki a JPA
automatikus sémagenerálását! Válassz egy tetszőleges felhasználónevet és jelszót az adatbázis eléréséhez! Biztosítsd
hozzá a szükséges jogokat!

Hozz létre egy új Maven projektet az IDEA-ban! Az `artifactId` legyen `jpa-socialnetwork-practice`! Hozd létre a megfelelő útvonalon a 
`persistence.xml` állományt és állítsd be a *persistence unit*ot!

A `socialnetwork` csomagban dolgozz!

## User entitás

Az entitások a JPA alapvető egységei, amelyek az objektum-relációs leképezésben vesznek részt. A JPA képes automatikus sémagenerálásra, ami azt
jelenti, hogy az alkalmazás indulásakor az entitások alapján létrehozza a szükséges táblákat. Ennek mikéntjét annotációkkal lehet konfigurálni.
Egy entitáshoz szükség van az osztályra tett `@Entity` annotációra, egy üres konstruktorra, és elsődleges kulcsra, ami akár több attribútumból álló
összetett kulcs is lehet.

Az alkalmazás középpontjában a felhasználók állnak. Hozd létre a `User` entitást, ami rendelkezik egy `Long id` egyedi azonosítóval,
egy `String userName` felhasználónévvel, `String password` jelszóval, és `String emailAddress` e-mail címmel! Legyen egy `Category category`
nevű attribútuma, ami egy enum, és a következő értékeket veheti fel: `FREE`, `PREMIUM`, `VIP`! Ügyelj rá, hogy szövegesen kerüljön mentésre!
Konfiguráld az oszlopok nevét konvenció szerint! A felhasználóneveknek egyedinek kell lenniük az adatbázisban.
Hozd létre a megfelelő konstruktorokat, valamint getter és setter metódusokat!

### Azonosító-generálás

Az azonosítók automatikus kiosztása többféle stratégia szerint történhet. Ezeket használhatod az alapértelmezett beállításokkal is, de lehetőséged
van konfigurálni is egyes generátorokat.

Az azonosítók táblagenerátorral kerüljenek kiosztásra minden további entitás esetén is, egyetlen `id_table` nevű táblában! Ehhez minden entitásban
add meg értelemszerűen ugyanazt a táblagenerátor.
Konfiguráld a következőképpen:
* az elsődleges kulcs az entitásokhoz tartozó tábla neve legyen
* a táblák neve a `table_name` oszlopba kerüljenek (elsődleges kulcs)
* a következő kiosztott érték az `id_value` oszlopba kerüljön
* az azonosítókat tízesével kérje le a keretrendszer

### Alapvető műveletek

Hozd létre a `UserDao` osztályt, ami az alapvető CRUD műveleteket tartalmazza! Legyen benne egy `void saveUser(User user)` metódus mentéshez,
egy `void deleteUserById(long userId)` törléshez, egy `User findUserById(long id)` olvasáshoz, és egy `boolean updateUser(long id, String newPassword)`
a jelszó módosításhoz! Utóbbi csak akkor végezzen frissítést, ha az új jelszó eltér! Ellenkező esetben `false` értékkel térjen vissza!
Legyen egy túltöltött változata a kategória módosításához is! Ahol lehet, használj referenciákat a műveletek során!

### Személyes adatok

A felhasználók személyes adatait a `PersonalData` beágyazható osztály tartalmazza! Ehhez hozd létre a `PersonalData personalData` attribútumot! 
Az osztály a `String realName`, `LocalDate dateOfBirth`, és `String city` adatokat tárolja! Próbáld meg kiszervezni egy másodlagos táblába, aminek a
neve `personal_data` legyen. A felhasználó azonosítóját a `user_id` oszlop tartalmazza!

### Regisztráció dátuma

A regisztráció dátumát a `LocalDate registrationDate` attribútum tárolja! Állítsd be, hogy a felhasználó mentése során automatikusan megkapja
az aktuális dátumot és azzal együtt kerüljön perzisztálásra! A teszteléshez használj egy `TimeMachine` osztályt, amitől előre beállított időpontot
lehet lekérni! Használhatod a megoldásban lévőt is, ami egy *singleton*.

## Posztok és kommentek (one-to-many)

Minden felhasználó bejegyzéseket tud közzétenni, amik a `Set<Post> posts` halmazba kerülnek. Egy posztnak rendelkeznie kell egy `Long id`
egyedi azonosítóval, a létrehozás dátumával (`LocalDateTime postDate`), egy enum `Content content` típussal, ami a `TEXT`, `IMAGE`, `VIDEO` példányokat
veheti fel! Figyelj az enum szöveges mentésére! Elég csak a tartalmat beállítani konstruktorban. A dátum perzisztálás előtt legyen beállítva automatikusan!

Konfigurálj kétirányú kapcsolatot a `User` (inverse side) és a `Post` (owner side) között! Legyen a `User` entitásnak egy `void addPost(Post post)`
metódusa, ami létre is hozza azt!

### Bejegyzések kezelése

Mivel az új felhasználóknak nincsenek posztjaik, ezért kaszkádolt mentést felesleges beállítani. Helyette a `PostDao` osztályban írj egy olyan
`void savePost(long userId, Post post)` metódust, ami a felhasználó referenciáján át elmenti az adott bejegyzést az adatbázisba!

Mivel a posztok bírnak a külső kulccsal, ezért egy felhasználó törlésére tett kísérlet kivételt eredményezne, mert utána azok nem létező felhasználóra mutatnának.
Ezért állíts be kaszkádolt törlést, hogy ezt elkerüld!

Legyen egy `List<Post> listAllPostsOfUser(long userId)` metódus, ami kilistázza időrendi sorrendben egy felhasználó azonosítója alapján a bejegyzéseit! Próbáld meg
úgy implementálni, hogy nem töltöd be a felhasználót a perzisztencia kontextusba!

Hozz létre egy `List<Post> listPostsOfUser(long userId, Predicate<Post> predicate)` metódust, ami tetszőleges feltétel alapján szűrt bejegyzéseket képes
visszaadni egy listában! Az eredményeket streamként kérd le!

### Kommentek létrehozása

A felhasználók hozzászólásokat írhatnak az egyes bejegyzések alá, amiket a `Comment` entitás példányai reprezentálnak.

A `Comment` entitás a következő attribútumokkal rendelkezzen:
* `Long id` egyedi azonosító
* `LocalDateTime commentDate` dátum
* `String commentText` szöveges tartalom
* `User user` felhasználó
* `Post post` bejegyzés

A konstruktorban csak a szöveges tartalmat kell megadni. A dátum a perzisztálás során automatikusan kerüljön beállításra! 
Konfiguráld megfelelően az entitást - állítsd be a táblagenerátort és ügyelj az attribútumok neveire! A komment szövegének hossza 8191 karakter lehet!

Alakíts ki kétirányú one-to-many kapcsolatokat! A `Post` (inverse side) `Set<Comment> comments` attribútuma tartalmazza a kommenteket, amire a `Comment` (owner side)
`Post post` attibútuma mutat vissza. Ne feledd, hogy a kommentek egyszerre a felhasználókhoz is tartoznak, ezért hozz létre a `User` (inverse side) entitásban egy
`Set<Comment> comments` attribútumot, amire a `Comment`-ben (owner side) a `User user` (inverse side) mutat! A kapcsolatok kiépítése a `Post` entitás feladata.
Az ebben lévő `void addComment(User user, Comment comment)` metódus végezze ezt el!

### Kommentek kezelése

A kommentekkel kapcsolatos adatbázis-műveletek a `CommentDao` osztályban kapnak helyet. Készíts egy `void saveComment(long userId, long postId, Comment comment)`
metódust az új hozzászólások mentéséhez! Használd bátran a referenciákat!

Az olvasást több irányból is meg lehet közelíteni. Írj egy `List<Comment> listCommentsOfUser(long userId)` metódust, amiben egy `NamedQuery`-t használsz a
felhasználó által írt kommentek listázásához! Készíts egy olyan `List<Comment> listCommentsUnderPostsByUser(long ownerId, long writerId)` metódust,
ami viszont egy felhasználó összes bejegyzéséhez fűzött összes komment közül listázza ki azokat, amiket egy adott felhasználó írt!

Legyen egy metódus, ami lapozásssal szűri az összes lekért hozzászólást (`List<Comment> listCommentsWithPaging(int firstIndex, int max)`)!

#### Az orphanRemoval használata

Az `ophanRemoval` az árván maradt objektumok automatikus törlését jelenti. Ez akkor történhet meg, ha egy gyermek objektumot eltávolítunk egy kollekcióból
a `remove()` metódussal. Így ha az `orphanRemoval = true` beállítás meg van adva a szülőben, akkor ez a rekordok szintjén is megjelenik anélkül, hogy az
`EntityManager` `remove()` metódusát használnod kellene. Ez akkor is működik, ha esetleg a gyermek objektum maga is kollekciót tartalmaz. Természetesen a
külső kulcsok megkötését itt sem lehet megsérteni, ezért azokban is meg kell adni az `orphanRemoval = true` beállítást.

**Vigyázz, a funkció bugos (hybernate-entitymanager 5.6.14.Final)! A `CascadeType.PERSIST` együttes megadása nélkül nem működik.**

Írj egy metódust a `PostDao` osztályban egy felhasználó összes bejegyzésének törlésére (`void deleteAllPostsByUserId(long userId)`)! Használd ki az
`orphanRemoval`-t! Próbáld ki kommentek hozzáadásával is!

#### Projection query

Készíts egy egyszerű `PostData` adatátviteli objektumot (DTO), ami a posztoló felhasználónevét (`String userName`), egy bejegyzés tartalmát(`Content content`), és
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

Konfiguráld az entitást a megfelelő attribútumokkal! A név legyen egyedi!

Alakíts ki kétirányú many-to-many kapcsolatot a `User` (owner side) és a `Group` (inverse side) között! Ehhez a `User` entitásban is vedd fel a
megfelelő attribútumot! A kapcsolótábla neve legyen `users_to_groups`, az oszlopai sorban `user_id` és `group_id` legyenek! A kapcsolatot a `Group`
példányok `void addUser(User user)` metódusa hozza létre!

### Csoportok kezelése

Készítsd el a `GroupDao` osztályban a `void saveGroup(Group group)`, a `void addUserToGroup(long userId, long groupId)`, és a `List<User> listGroupMembers(long groupId)`
nevű metódusokat! Arra az esetre is írj egyet, ha egy felhasználó ki akar lépni egy csoportból (`void removeUserFromGroup(long userId, long groupId)`)!

## Ismerősök hozzáadása (many-to-many)

Egy felhasználónak lehetnek ismerősei is az oldalon. Lehetőséged van kialakítani egy entitáson belül is many-to-many kapcsolatot. Ilyenkor az owner és az inverse side
ugyanaz, de a `mappedBy` elem nem használható egyszerre két oldalon, ezért ne tedd azt ki! A kapcsolótáblát azonban ugyanúgy konfigurálhatod a `@JoinTable` annotációval.

Készíts egy `Set<User> friends` attribútumot a `User` entitáson belül! Konfigurálj many-to-many kapcsolatot, és állítsd be a tábla és az oszlopok nevét! Hozd létre a
`void addFriend(User user)` metódust, amivel két felhasználót egymás ismerőseinek listájához adhatsz!

A `UserDao` osztályban legyen egy `void saveFriendship(long userId1, long userId2)`, egy `List<User> listFriendsOfUser(long userId)`, és egy `void removeFriendship(long userId1, long userId2)`
metódus az ismerősök mentésére, egy felhasználó ismerőseinek listázására, és egy kapcsolat törlésére!

## Fetch és entitásgráfok

A kapcsolatok betöltése vagy `FetchType.EAGER`, vagy `FetchType.LAZY` típusú alapértelmezetten, a kapcsolatok oldalától függően. A kettő közti finomhangolást entitásgráfokkal
lehet megoldani ahelyett, hogy a fetch-elést a JPQL utasításba égetnéd be. Emellett egy előre megírt gráfot több lekérdezéshez is használhatsz. Az entitásgráfokat propertyk (kulcs-érték
párok formájában lehet megadni egy lekérdezés során. Lehetőség van az alapértelmezett betöltést teljesen felülíró (`javax.persistence.fetchgraph`), vagy azt kiegészítő (`javax.persistence.loadgraph`)
property kulcshoz hozzárendelni egy adott entitásgráfot. A gráfokat statikusan, az entitásra tett annotációkkal tudod deklarálni, vagy programozottan metódusokkal is létrehozhatsz
egy konkrét `EntityGraph` példányt. 

Használatuk az `EntityManager` `find()` és `createQuery()` metódusában lehetséges, ezért készíts a `GroupDao` osztályban egy `List<Group> listGroupsWithNamedGraph(String graphName)` metódust,
aminek egy statikus entitásgráf nevét lehet átadni! A `find()` metódus harmadik paraméterének egy olyan `Map<String, Object>` példányt adhatsz át, aminek a kulcsa a fentebb említett property
kulcs egyike, az értéke pedig maga az `EntityGraph` példány, amit az `EntityManager` `createEntityGraph()` metódussal lehet létrehozni. Ha a gráfot statikusan deklaráltad, akkor elég annak csak
a nevét átadni neki paraméterül. A `createQuery()` metódusnál azonban nincs szükséged külön `Map`-re. Itt a property-t a `TypedQuery` `setHint(String hintName, Object value)` metódusával tudod
beállítani, hasonlóan a paraméterezett lekérdezések paraméteréhez.
A programozott megadáshoz szintén az `EntityManager` `createEntityGraph()` metódusát kell használni, azonban itt annak az entitásnak az osztályát kell paraméterül adni, amihez a gráfot készíted.
Ezután a visszaadott `EntitíGraph` példány metódusai használhatók a gráf kialakítására.

Az entitásgráfok node-jai az entitások attribútuma. A kollekció típusú attribútumokat azonban tovább lehet bontani algráfokra, így akár egyetlen `SELECT` utasítással kollekciók egész láncolatát
kérheted le. Ha statikusan készíted, akkor ezeknek külön nevet is kell adni, míg dinamikusan nincs erre szükség.

Készíts egy statikus gráfot a `Group` entitáshoz, amivel egy csoportba tartozó felhasználókat és azok bejegyzéseit lehet betölteni!

Próbáld ki programozottan is! Hozz létre egy `Group findGroupWithDynamicGraph(long groupId)` metódust, ami id alapján visszaad egy csoportot a tagokkal és azok ismerőseivel együtt betöltve! 

## Service réteg kialakítása

(talán később)
* SHA-256
* e-mail cím ellenőrzés
* PREMIUM, VIP funkciók
* felhasználói jogok, keresések
* Mockito
* nemtom
