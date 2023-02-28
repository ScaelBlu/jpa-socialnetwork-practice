# JPA adatbázis-programozási gyakorlat - Social network

Ebben a projektben egy közösségi oldal sémáján keresztül lehet gyakorolni az adatbázis műveleteket JPA-val.
Használd a projektet szabadon egyfajta "homokozóként", amiben a JPA működését megismerheted! Egészítsd ki a saját metódusaiddal!
A projekt célja elsősorban segíteni a JPA működésének megértését, betanítani a használatát, és ösztönözni az egyéni tanulást, nem
pedig valós, gyakorlatban alkalmazott megoldások bemutatása. A repóban lévő információk és implementációk helyessége nem garantált -
saját felelősségedre használd! Feltöltéskor a tesztek lefutottak, de ennek ellenére is tartalmazhat hibákat. A feladatok megoldását
a repository `src` mappájában találod. Mind a feladatok, mind a megoldások a saját munkám eredményei. A leírtak a Hibernate működésére
vonatkoznak. Más JPA implementációk ettől eltérhetnek.

Törekedj a clean code elvek betartására! Írj teszteket is, ha szeretnéd, írd meg őket előre. Használd bátran az AssertJ-t is!

Jó gyakorlást!

## Előkészületek

Hozz létre egy új adatbázis sémát `socialnetwork` névvel! Ebben legyenek a generált táblák. Használd ki a JPA
automatikus sémagenerálását! Válassz egy tetszőleges felhasználónevet és jelszót az adatbázis eléréséhez! Biztosítsd
hozzá a szükséges jogokat!

Hozz létre egy új Maven projektet az IDEA-ban! Az `artifactId` legyen `jpa-socialnetwork-practice`! Hozd létre a megfelelő útvonalon a 
`persistence.xml` állományt és állítsd be a *persistence unit*ot!

A `socialnetwork` csomagban dolgozz!

## User entitás

>Az entitások az objektum-relációs leképezés (ORM) alapvető egységei a JPA-ban. A JPA képes automatikus sémagenerálásra, ami azt jelenti, hogy az
alkalmazás indulásakor az entitások alapján legenerálja és lefuttatja azokat az SQL utasításokat, amik létrehozzák a táblákat. A táblákat
annotációkkal lehet konfigurálni. Egy entitáshoz szükség van az osztályra tett `@Entity` annotációra, egy üres konstruktorra, és elsődleges kulcsra,
ami akár több attribútumból álló összetett kulcs is lehet.

A projekt központjában a felhasználók állnak. Hozd létre a `User` entitást, ami rendelkezik egy `Long id` egyedi azonosítóval, egy `String userName`
felhasználónévvel, `String password` jelszóval, és `String emailAddress` e-mail címmel! Legyen egy `Category category` nevű attribútuma, ami egy enum,
és a következő értékeket veheti fel: `FREE`, `PREMIUM`, `VIP`! Ügyelj rá, hogy szövegesen kerüljön mentésre! Konfiguráld az oszlopok nevét konvenció
szerint! A felhasználóneveknek egyedinek kell lenniük az adatbázisban. Az e-mail cím hossza maximum 100 karakter legyen! Hozd létre a szükséges
konstruktorokat, valamint getter és setter metódusokat!

### Azonosító-generálás

>Az azonosítók automatikus kiosztása többféle stratégia szerint történhet. Ezeket használhatod az alapértelmezett beállításokkal is, de lehetőséged
van konfigurálni is egyes generátorokat. A `GenerationType.TABLE` esetén egy új tábla jön létre, ami egy szekvenciát utánoz. Ilyenkor a keretrendszer
a táblából kéri le egy `SELECT` utasítással a következő értéket, és frissíti is a táblát. Mivel ez erőforrásigényes, ezért be lehet állítani, hogy
hány darab azonosítót tároljon el előre a memóriában. Ez a stratégia azoknál az adatbázisoknál is működik, amelyek nem támogatják a szekvenciákat,
és emiatt a `GenerationType.SEQUENCE` nem használható. A `GenerationType.IDENTITY` stratégia az azonosító kiosztását az adatbázisra bízza, azaz csak
mentés után kapja meg. Emiatt egymás után hívott `persist()` és `detach()` metódus után az entitás mentésre kerül, míg táblagenerálással nem.

Az azonosítók táblagenerátorral kerüljenek kiosztásra minden további entitás esetén is, egyetlen `id_table` nevű táblában! Ehhez minden entitásban
állítsd be ugyanúgy a táblagenerátor. A konfiguráció a `@TableGenerator` annotáció elemeivel lehetséges.
Konfiguráld a következőképpen:
* az elsődleges kulcs az entitásokhoz tartozó tábla neve legyen
* a táblák neve a `table_name` oszlopba kerüljenek (elsődleges kulcs)
* a következő kiosztott érték az `id_value` oszlopba kerüljön
* az azonosítókat tízesével kérje le a keretrendszer

Add meg a generátor nevét a generált érték beállításához (`@GeneratedValue`)!

### Alapvető műveletek

Hozd létre a `UserDao` osztályt, ami az alapvető CRUD műveleteket tartalmazza! Legyen benne egy `void saveUser(User user)`* metódus mentéshez,
egy `void deleteUser(long userId)` törléshez, egy `User findUser(long id)` olvasáshoz, és egy `void updateUser(long id, String newPassword)`
a jelszó módosításhoz! Legyen egy túltöltött változata a kategória módosításához is! Ahol lehet, használj referenciákat a műveletek során!

*A könnyebb teszteléshez elkészítheted `void saveUsers(User user, User... users)` változatban is!

### Személyes adatok

>A beágyazható osztályok nem valódi entitások, hanem csak kiegészítenek egy másikat, azaz annak a részét képezik. Ehhez elég az `@Embeddable`
annotációval ellátni őket, és az attribútumait ugyanúgy konfigurálhatod. Fontos, hogy legyen üres konstruktora. Ha az entitásban felveszel egy
beágyazott osztályt attribútumként, akkor az `@Embedded` annotációval kell ellátni.

A felhasználók személyes adatait a `PersonalData` beágyazható osztály tartalmazza! Ehhez hozd létre a `PersonalData personalData` attribútumot! 
Az osztály a `String realName`, `LocalDate dateOfBirth`, és `String city` adatokat tárolja! Próbáld meg kiszervezni egy másodlagos táblába, aminek a
neve `personal_data` legyen. A felhasználó azonosítóját a `user_id` oszlop tartalmazza!

### Regisztráció dátuma

A regisztráció dátumát a `LocalDate registrationDate` attribútum tárolja! Állítsd be, hogy a felhasználó mentése során automatikusan megkapja
az aktuális dátumot és azzal együtt kerüljön perzisztálásra! 

A teszteléshez használj egy `TimeMachine` osztályt, amitől előre beállított időpontot lehet lekérni! Használhatod a megoldásban lévőt is, ami
egy *singleton*, azaz csak egyetlen példánya lehet.

## Posztok és kommentek (one-to-many)

>Az entitások között kapcsolatokat lehet kialakítani, amelyek egy-, vagy kétirányúak lehetnek. Kétirányú kapcsolatoknál az ún. *owner side* hordozza
a táblák szintjén a külső kulcsot, ami a másik tábla - az *inverse side* - rekordjára mutat. Úgy is mondhatni, hogy az *inverse side* "leképződik" az
*owner side*-on, amit az annotációk `mappedBy` elemével lehet megadni. Kétirányú *one-to-many* kapcsolat esetén mindig a kollekciót tartalmazó az
*inverse side*!
>
>Egyirányú kapcsolatoknál nincs értelme oldaliságról beszélni. Egyirányú *one-to-one* kapcsolatoknál mindkét oldal hordoz a másikra mutató kulcsot.
Egyirányú *one-to-many* kapcsolatoknál kapcsolótáblán keresztül valósul meg az összeköttetés, míg az egyirányú *many-to-many* kapcsolatoknál két
kapcsolótábla is létrejön egymást tükrözve. Kétirányú *many-to-many* kapcsolatnál egy kapcsolótábla jön létre, és a `mappedBy` határozza meg, hogy
melyik oldal kulcsa legyen az első oszlopban.
>
>*Many-to-many* kapcsolatot egy entitáson belül is létre lehet hozni (lásd később).

A felhasználók bejegyzéseket tudnak közzétenni az oldalon, amik a `Set<Post> posts` halmazba kerülnek. Egy posztnak rendelkeznie kell egy `Long id`
egyedi azonosítóval, a létrehozás dátumával (`LocalDateTime postDate`), egy enum `Content content` típussal, ami a `TEXT`, `IMAGE`, `VIDEO` példányokkal
rendelkezik. Figyelj az enum szöveges mentésére! Elég csak a tartalmat beállítani konstruktorban. A dátum perzisztálás előtt legyen beállítva automatikusan!

Konfigurálj kétirányú kapcsolatot a `User` (*inverse side*) és a `Post` (*owner side*) között! Legyen a `User` entitásnak egy `void addPost(Post post)`
metódusa, ami létre is hozza azt!

### Bejegyzések kezelése

Az új felhasználóknak nincsenek posztjaik, ezért kaszkádolt mentést felesleges beállítani. Helyette a `PostDao` osztályban írj egy olyan
`void savePostToUser(long userId, Post post)` metódust, ami a felhasználó referenciáját használva elmenti az adott bejegyzést az adatbázisba!

Mivel a posztok bírnak a külső kulccsal, ezért egy felhasználó törlésére tett kísérlet `ConstraintViolationException` kivételt eredményezne, mert utána
azok nem létező felhasználóra mutatnának. Ezért állíts be kaszkádolt törlést, hogy ezt elkerüld! Kaszkádolt törlés során nincs szükség betölteni a példányhoz
kapcsolódó többi példányt, ezért használhatsz referenciát is, vagy egy egyszerű `DELETE` query-t.

Legyen egy `List<Post> listAllPostsOfUser(long userId)` metódus, ami kilistázza időrendi sorrendben egy felhasználó azonosítója alapján a bejegyzéseit!
Próbáld meg úgy implementálni, hogy nem töltöd be a felhasználót a perzisztencia kontextusba!

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

A konstruktorban csak a szöveges tartalmat kell megadni. A dátum a perzisztálás során automatikusan kerüljön beállításra! Konfiguráld megfelelően az entitást -
állítsd be a táblagenerátort és ügyelj az attribútumok neveire! A komment szövegét `TEXT` típusként kerüljön az adatbázisba! Ezt a `columnDefinition` elemmel
adhatod meg.

Alakíts ki kétirányú *one-to-many* kapcsolatokat! A `Post` (*inverse side*) `Set<Comment> comments` attribútuma tartalmazza a kommenteket, amire a `Comment`
`Post post` (*owner side*) attibútuma mutat vissza. Ne feledd, hogy a kommentek egyszerre a felhasználókhoz is tartoznak, ezért hozz létre a `User` entitásban
egy `Set<Comment> comments` attribútumot, amire a `Comment`-ben (*owner side*) a `User user` (*inverse side*) mutat! A kapcsolatok kiépítése a `Post` entitás
feladata legyen. Az ebben lévő `void addComment(User user, Comment comment)` metódus végezze ezt el! Gondolj a törlésekre is!

### Kommentek kezelése

A kommentekkel kapcsolatos adatbázis-műveletek a `CommentDao` osztályban kapnak helyet. Készíts egy `void saveComment(long userId, long postId, Comment comment)`
metódust az új hozzászólások mentéséhez! Használd bátran a referenciákat!

Az adatbázisból olvasást több irányból is meg lehet közelíteni. Írj egy `List<Comment> listCommentsOfUser(long userId)` metódust, amiben egy `NamedQuery`-t
használsz a felhasználó által írt kommentek listázásához! A `NamedQuery` egy egyszerű JPQL query, amit névvel ellátva statikusan deklarálsz egy entitásra tett
`@NamedQuery` annotációban, így már az entitás betöltésekor ellenőrzésre kerül a szintaxisa.

Készíts egy olyan `List<Comment> listCommentsUnderPostsByUser(long ownerId, long writerId)` metódust, ami viszont egy felhasználó összes bejegyzéséhez fűzött
összes komment közül időrendben listázza ki azokat, amiket egy adott felhasználó írt!

Legyen egy metódus, ami lapozásssal szűri az összes lekért hozzászólást (`List<Comment> listCommentsWithPaging(int firstIndex, int max)`)!

#### Az orphanRemoval használata

>Az `ophanRemoval` az árván maradt objektumok automatikus törlését jelenti. Ez akkor történhet meg, ha egy gyermek objektumot eltávolítunk egy kollekcióból
a **kollekció** `remove()` metódusával. Így ha az `orphanRemoval = true` beállítás meg van adva a szülőben, akkor ez a rekordok szintjén is megjelenik anélkül,
hogy az `EntityManager` `remove()` metódusát használnod kellene. Ez akkor is működik, ha esetleg a gyermek objektum maga is kollekciót tartalmaz. Természetesen
a külső kulcsok megkötését itt sem lehet megsérteni, ezért azokban is meg kell adni az `orphanRemoval = true` beállítást.
>
>>**Vigyázz, a funkció bugos (hybernate-entitymanager 5.6.14.Final)! A `CascadeType.PERSIST` együttes megadása nélkül nem működik.**

Írj egy metódust a `PostDao` osztályban egy felhasználó összes bejegyzésének törlésére (`void deleteAllPostsByUserId(long userId)`)! Használd ki az
`orphanRemoval`-t! Próbáld ki kommentek hozzáadásával is!

#### Projection query

>A DTO-k - *Data transfer object* - egyszerű objektumok egyszerű típusokkal, aminek a célja az alkalmazás rétegei között, vagy akár a hálózaton keresztül küldött
információk egy egységbe zárása. Utóbbihoz szerializálhatónak kell lennie, ami bájtok vagy karakterek sorozatára történő átalakítást jelent.

Készíts egy egyszerű `PostData` adatátviteli objektumot, ami a posztoló felhasználónevét (`String userName`), egy bejegyzés tartalmát(`Content content`), és
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

Alakíts ki kétirányú many-to-many kapcsolatot a `User` (*owner side*) és a `Group` (*inverse side*) között! Ehhez a `User` entitásban is vedd fel a megfelelő
attribútumot! A kapcsolótábla neve legyen `users_to_groups`, az oszlopai sorban `user_id` és `group_id` legyenek! A kapcsolatot a `Group` példányok `void addUser(User user)`
metódusa hozza létre!

### Csoportok kezelése

Készítsd el a `GroupDao` osztályban a `void saveGroup(Group group)`, a `void addUserToGroup(long userId, long groupId)`, és a `List<User> listGroupMembers(long groupId)`
nevű metódusokat! Arra az esetre is írj egyet, ha egy felhasználó ki akar lépni egy csoportból (`void removeUserFromGroup(long userId, long groupId)`)!

Ha törölni akarnak egy csoportot, az nem járhat együtt a felhasználók törlésével. Írj egy `void deleteGroup(long groupId)` metódust!

## Szociális háló (many-to-many)

Egy felhasználónak lehetnek ismerősei is az oldalon. Lehetőséged van kialakítani egy entitáson belül is *many-to-many* kapcsolatot. Ilyenkor - mivel csak egy
oldal van - a `mappedBy` elem használata kivételt eredményez. A kapcsolótáblát azonban ugyanúgy konfigurálhatod a `@JoinTable` annotációval. A tábla ilyenkor
kétszer is tartalmazza a kapcsolatot - egyszer az egyik, majd a másik példány irányából.

Készíts egy `Set<User> friends` attribútumot a `User` entitáson belül! Konfigurálj *many-to-many* kapcsolatot, és állítsd be a tábla és az oszlopok nevét! Hozd
létre a `void addFriend(User user)` metódust, amivel két felhasználót egymás ismerőseinek listájához adhatsz!

A `UserDao` osztályban legyen egy `void saveFriendship(long userId1, long userId2)`, egy `List<User> listFriendsOfUser(long userId)`, és egy
`void removeFriendship(long userId1, long userId2)` metódus az ismerősök mentésére, egy felhasználó ismerőseinek listázására, és egy kapcsolat törlésére!

## Fetch és entitásgráfok

>A kapcsolatok betöltése vagy `FetchType.EAGER`, vagy `FetchType.LAZY` típusú alapértelmezetten a kapcsolatok oldalától függően. A kettő közti finomhangolást
entitásgráfokkal lehet megoldani ahelyett, hogy a fetch-elést a JPQL utasításba égetnéd be. Emellett egy előre megírt gráfot több lekérdezésben is használhatsz.
Az entitásgráfokat property-k, azaz kulcs-érték párok formájában lehet megadni egy lekérdezés során. Lehetőség van az alapértelmezett betöltést teljesen felülíró
(`javax.persistence.fetchgraph`), vagy azt kiegészítő (`javax.persistence.loadgraph`) property kulcshoz hozzárendelni egy adott entitásgráfot. Gráfot statikusan,
az entitásra tett `@NamedEntityGraph` annotációval tudsz deklarálni, vagy programozottan metódusokkal is létrehozhatsz egy konkrét `EntityGraph` példányt. 
>
>Az entitásgráfok node-jai az entitások attribútumai. A kollekció típusú attribútumokat azonban tovább lehet bontani algráfokra, így akár egyetlen `SELECT`
utasítással kollekciók egész láncolatát kérheted le. Ha statikusan készíted, akkor ezeknek külön nevet is kell adni, míg dinamikusan nincs erre szükség.
>
>Használatuk az `EntityManager` `find()` és `createQuery()` metódusában lehetséges. A `find()` metódus harmadik paraméterként egy olyan `Map<String, Object>` példányt
vár, aminek a kulcsa a fentebb említett property kulcs egyike, az értéke pedig maga az `EntityGraph` példány. Egy gráfot az `EntityManager` `createEntityGraph()`
metódusával lehet példányosítani egy gráf nevét átadva, ha azt már statikusan deklaráltad. A `createQuery()` metódusnál azonban nincs szükséged külön `Map`-re.
Itt a property-t a `TypedQuery` `setHint(String hintName, Object value)` metódusával tudod beállítani, hasonlóan a paraméterezett lekérdezések paraméteréhez.

A programozott megadáshoz szintén az `EntityManager` `createEntityGraph()` metódusát kell használni, azonban itt annak az entitásnak az osztályát add paraméterül,
amihez a gráfot készíted. Ezután a kapott `EntitíGraph` példány metódusai használhatók a node-ok és algráfok kialakítására.

Készíts a `GroupDao` osztályban egy `List<Group> listGroupsWithNamedGraph(String graphName)` metódust, aminek egy entitásgráf nevét lehet átadni! Készíts egy statikus
gráfot a `Group` entitáshoz, amivel egy csoportba tartozó felhasználókat és azok bejegyzéseit lehet betölteni!

Próbáld ki programozottan is! Hozz létre egy `Group findGroupWithDynamicGraph(long groupId)` metódust, ami id alapján visszaad egy csoportot a tagokkal és azok ismerőseivel
együtt betöltve! 

## Service réteg kialakítása

(talán később)
* SHA-256
* e-mail cím ellenőrzés
* PREMIUM, VIP funkciók
* felhasználói jogok, keresések
* Mockito
* nemtom
