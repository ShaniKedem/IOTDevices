package IOTDevices;

public interface ICrud<ID, T> {
    ID create (T record);
    T read (ID id);
    ID update (T record);
    void delete (ID id);
}
