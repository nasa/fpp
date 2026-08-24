locate constant a at "a.fpp"
locate constant b at "b.fpp"
locate dictionary constant d at "d.fpp"

locate type T at "T.fpp"
locate type Tp at "Tp.fpp"
locate type FwSizeStoreType at "FwSizeStoreType.fpp"
locate dictionary type T2 at "T2.fpp"

vector V1 = [Tp size a] T default b
vector V2 = [size a] T
dictionary vector V3 = [size d] T2
