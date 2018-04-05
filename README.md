# Plastic-Turtle-Militia-MDSD-2018

# Számítógép hálózat modellező

  

A cél egy számítógép hálózatok tervezését és karban tartását segítő eszköz készítése. A környezet alapvetően tervezést könnyítené meg azzal, hogy a hálózatban található eszközöket könnyen átlátható formába jelenítené meg. Emellett a megadott adatokból két mérő számot is szolgáltatna. Mindkettőt csak két végpont között képes elvégezni. Az egyik a maximális átviteli sebesség, a másik a leggyorsabb út.

A hálózatban háromféle entitás lehet jelen végpontok, hálózati eszközök és összeköttetések. A végpontok, valamilyen számítógépek amik közt a kommunikáció folyhat, ezek lehetnek egyszerű asztali PC-k, okos telefonok vagy szerverek. A hálózati eszközökön folyik át a forgalom és ők irányítják a megfelelő útvonalakon. Az összeköttetés lehet vezeték vagy vezeték nélküli kapcsolat.

A végpontoknak lesznek hálózati tulajdonságai, mint logikai cím(ek) és csatlakozok száma és típusai. A “számítógépek” két halmazra lehet bontani a kliensekre és a szerverekre. Ez a két halmaz közötti kommunikációt fogjuk becsülni. A forgalom összeköttetéseken és forgalom irányítókon folyik. Előbbinek áteresztő képessége és a csatlakozója a megadható jellemzője és mindig két eszköz között helyezkedik el. A hálózati eszközök adják a késleltetést a kapcsolatokhoz. Emellett meghatározható hány darab milyen csatlakozóval rendelkeznek, mekkora sávszélességet képesek kezelni és milyen logika címmel rendelkeznek.

A hálózat definiálása két felületen történik. A grafikus felületet a topológiai elrendezést lehet megadni. Például hálózati eszközök, végpontok száma és a köztük található összeköttetések. Szöveges formában lehet megadni, hogy egy elemnek milyen tulajdonságai vannak.

Mindkét módszerrel elkészült modellt validálhatjuk különböző jólformáltsági kényszerek és figyelmeztetések mentén:

--   Kényszerek:
    

-   Minden végpont elérhető legalább egy másik végpontból, illetve minden hálózat eszköz elérhető legalább kettő végpontból.
    
-   Az összeköttetés két végén azonos csatlakozó van.
    
-   A késleltetés nem negatív, a sávszélesség pedig pozitív értékű.
    
-   A modellben nincs két azonos IP című elem.
    
-   Az egy alhálózatba tartozó IP címek azonos címtartományban vannak.
    

--   Figyelmeztetés:
    

-   Két közvetlenül összekötött eszköz között nem a lehető leggyorsabb összeköttetés van.