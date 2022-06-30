class T {
  public:
    enum {
      SERIALIZED_SIZE = 1
    };

    bool operator==(const T& obj) const;
    bool operator!=(const T& obj) const;
};
