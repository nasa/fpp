#include </StructOK2SerializableAc.hpp>
#include <Fw/Types/Assert.hpp>
#include <Fw/Types/BasicTypes.hpp>
#if FW_SERIALIZABLE_TO_STRING
#include <Fw/Types/EightyCharString.hpp>
#endif
#include <cstring>
// public methods

StructOK2::StructOK2(void): Serializable() {

}

StructOK2::StructOK2(const StructOK2& src) : Serializable() {
    this->set(src.m_s1);
}

StructOK2::StructOK2(const StructOK2* src) : Serializable() {
    FW_ASSERT(src);
    this->set(src->m_s1);
}

StructOK2::StructOK2(const StructOK1& s1) : Serializable() {
    this->set(s1);
}

const StructOK2& StructOK2::operator=(const StructOK2& src) {
    this->set(src.m_s1);
    return src;
}

bool StructOK2::operator==(const StructOK2& src) const {
    return (
        (src.m_s1 == this->m_s1) &&
        true);
}

void StructOK2::set(const StructOK1& s1) {
    this->m_s1 = s1;
}

StructOK1 StructOK2::gets1(void) {
    return this->m_s1;
}

void StructOK2::sets1(StructOK1 val) {
    this->m_s1 = val;
}
Fw::SerializeStatus StructOK2::serialize(Fw::SerializeBufferBase& buffer) const {
    Fw::SerializeStatus stat;

#if FW_SERIALIZATION_TYPE_ID
    // serialize type ID
    stat = buffer.serialize((U32)StructOK2::TYPE_ID);
#endif

    stat = buffer.serialize(this->m_s1);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    return stat;
}

Fw::SerializeStatus StructOK2::deserialize(Fw::SerializeBufferBase& buffer) {
    Fw::SerializeStatus stat;

#if FW_SERIALIZATION_TYPE_ID
    U32 typeId;

    stat = buffer.deserialize(typeId);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }

    if (typeId != StructOK2::TYPE_ID) {
        return Fw::FW_DESERIALIZE_TYPE_MISMATCH;
    }
#endif

    stat = buffer.deserialize(this->m_s1);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    return stat;
}

#if FW_SERIALIZABLE_TO_STRING  || BUILD_UT

void StructOK2::toString(Fw::StringBase& text) const {

    static const char * formatString =
       "("
       "s1 = %s"
       ")";

    // declare strings to hold any serializable toString() arguments


    Fw::EightyCharString s1Str;
    this->m_s1.toString(s1Str);

    char outputString[FW_SERIALIZABLE_TO_STRING_BUFFER_SIZE];
    (void)snprintf(outputString,FW_SERIALIZABLE_TO_STRING_BUFFER_SIZE,formatString
       ,s1Str.toChar()
    );
    outputString[FW_SERIALIZABLE_TO_STRING_BUFFER_SIZE-1] = 0; // NULL terminate

    text = outputString;
}

#endif

#ifdef BUILD_UT
    std::ostream& operator<<(std::ostream& os, const StructOK2& obj) {
        Fw::EightyCharString str;
        obj.toString(str);
        os << str.toChar();
        return os;
    }
#endif
