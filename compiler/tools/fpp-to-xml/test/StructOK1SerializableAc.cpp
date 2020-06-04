#include </StructOK1SerializableAc.hpp>
#include <Fw/Types/Assert.hpp>
#include <Fw/Types/BasicTypes.hpp>
#if FW_SERIALIZABLE_TO_STRING
#include <Fw/Types/EightyCharString.hpp>
#endif
#include <cstring>

    StructOK1::m_stringString::m_stringString(const char* src): StringBase() {
        this->copyBuff(src,this->getCapacity());
    }

    StructOK1::m_stringString::m_stringString(const Fw::StringBase& src): StringBase() {
        this->copyBuff(src.toChar(),this->getCapacity());
    }

    StructOK1::m_stringString::m_stringString(const m_stringString& src): StringBase() {
        this->copyBuff(src.toChar(),this->getCapacity());
    }

    StructOK1::m_stringString::m_stringString(void): StringBase() {
        this->m_buf[0] = 0;
    }

    StructOK1::m_stringString::~m_stringString(void) {
    }

    bool StructOK1::m_stringString::operator==(const m_stringString& src) const {
        return (0 == strncmp(this->m_buf,src.m_buf,sizeof(this->m_buf)));
    }

    NATIVE_UINT_TYPE StructOK1::m_stringString::length(void) const {
        return (NATIVE_UINT_TYPE)strnlen(this->m_buf,sizeof(this->m_buf));
    }

    const char* StructOK1::m_stringString::toChar(void) const {
        return this->m_buf;
    }

    void StructOK1::m_stringString::copyBuff(const char* buff, NATIVE_UINT_TYPE size) {
        FW_ASSERT(buff);
        // check for self copy
        if (buff != this->m_buf) {
            (void)strncpy(this->m_buf,buff,size);
            // NULL terminate
            this->terminate(sizeof(this->m_buf));
        }
    }

    Fw::SerializeStatus StructOK1::m_stringString::serialize(Fw::SerializeBufferBase& buffer) const {
        NATIVE_UINT_TYPE strSize = strnlen(this->m_buf,sizeof(this->m_buf));
        // serialize string
        return buffer.serialize((U8*)this->m_buf,strSize);
    }

    Fw::SerializeStatus StructOK1::m_stringString::deserialize(Fw::SerializeBufferBase& buffer) {
        NATIVE_UINT_TYPE maxSize = sizeof(this->m_buf);
        // deserialize string
        Fw::SerializeStatus stat = buffer.deserialize((U8*)this->m_buf,maxSize);
        // make sure it is null-terminated
        this->terminate(maxSize);

        return stat;
    }

    NATIVE_UINT_TYPE StructOK1::m_stringString::getCapacity(void) const {
        return sizeof(this->m_buf);
    }

    void StructOK1::m_stringString::terminate(NATIVE_UINT_TYPE size) {
        // null terminate the string
        this->m_buf[size < sizeof(this->m_buf)?size:sizeof(this->m_buf)-1] = 0;
    }

    const StructOK1::m_stringString& StructOK1::m_stringString::operator=(const StructOK1::m_stringString& other) {
        this->copyBuff(other.m_buf,this->getCapacity());
        return *this;
    }

// public methods

StructOK1::StructOK1(void): Serializable() {

}

StructOK1::StructOK1(const StructOK1& src) : Serializable() {
    this->set(src.m_mF32, src.m_mF64, src.m_mI16, src.m_mI32, src.m_mI64, src.m_mI8, src.m_mU16, src.m_mU32, src.m_mU64, src.m_mU8, src.m_m_bool, src.m_m_string);
}

StructOK1::StructOK1(const StructOK1* src) : Serializable() {
    FW_ASSERT(src);
    this->set(src->m_mF32, src->m_mF64, src->m_mI16, src->m_mI32, src->m_mI64, src->m_mI8, src->m_mU16, src->m_mU32, src->m_mU64, src->m_mU8, src->m_m_bool, src->m_m_string);
}

StructOK1::StructOK1(F32 mF32, F64 mF64, I16 mI16, I32 mI32, I64 mI64, I8 mI8, U16 mU16, U32 mU32, U64 mU64, U8 mU8, bool m_bool, const StructOK1::m_stringString& m_string) : Serializable() {
    this->set(mF32, mF64, mI16, mI32, mI64, mI8, mU16, mU32, mU64, mU8, m_bool, m_string);
}

const StructOK1& StructOK1::operator=(const StructOK1& src) {
    this->set(src.m_mF32, src.m_mF64, src.m_mI16, src.m_mI32, src.m_mI64, src.m_mI8, src.m_mU16, src.m_mU32, src.m_mU64, src.m_mU8, src.m_m_bool, src.m_m_string);
    return src;
}

bool StructOK1::operator==(const StructOK1& src) const {
    return (
        (src.m_mF32 == this->m_mF32) &&
        (src.m_mF64 == this->m_mF64) &&
        (src.m_mI16 == this->m_mI16) &&
        (src.m_mI32 == this->m_mI32) &&
        (src.m_mI64 == this->m_mI64) &&
        (src.m_mI8 == this->m_mI8) &&
        (src.m_mU16 == this->m_mU16) &&
        (src.m_mU32 == this->m_mU32) &&
        (src.m_mU64 == this->m_mU64) &&
        (src.m_mU8 == this->m_mU8) &&
        (src.m_m_bool == this->m_m_bool) &&
        (src.m_m_string == this->m_m_string) &&
        true);
}

void StructOK1::set(F32 mF32, F64 mF64, I16 mI16, I32 mI32, I64 mI64, I8 mI8, U16 mU16, U32 mU32, U64 mU64, U8 mU8, bool m_bool, const StructOK1::m_stringString& m_string) {
    this->m_mF32 = mF32;
    this->m_mF64 = mF64;
    this->m_mI16 = mI16;
    this->m_mI32 = mI32;
    this->m_mI64 = mI64;
    this->m_mI8 = mI8;
    this->m_mU16 = mU16;
    this->m_mU32 = mU32;
    this->m_mU64 = mU64;
    this->m_mU8 = mU8;
    this->m_m_bool = m_bool;
    this->m_m_string = m_string;
}

F32 StructOK1::getmF32(void) {
    return this->m_mF32;
}

F64 StructOK1::getmF64(void) {
    return this->m_mF64;
}

I16 StructOK1::getmI16(void) {
    return this->m_mI16;
}

I32 StructOK1::getmI32(void) {
    return this->m_mI32;
}

I64 StructOK1::getmI64(void) {
    return this->m_mI64;
}

I8 StructOK1::getmI8(void) {
    return this->m_mI8;
}

U16 StructOK1::getmU16(void) {
    return this->m_mU16;
}

U32 StructOK1::getmU32(void) {
    return this->m_mU32;
}

U64 StructOK1::getmU64(void) {
    return this->m_mU64;
}

U8 StructOK1::getmU8(void) {
    return this->m_mU8;
}

bool StructOK1::getm_bool(void) {
    return this->m_m_bool;
}

const StructOK1::m_stringString& StructOK1::getm_string(void) {
    return this->m_m_string;
}

void StructOK1::setmF32(F32 val) {
    this->m_mF32 = val;
}
void StructOK1::setmF64(F64 val) {
    this->m_mF64 = val;
}
void StructOK1::setmI16(I16 val) {
    this->m_mI16 = val;
}
void StructOK1::setmI32(I32 val) {
    this->m_mI32 = val;
}
void StructOK1::setmI64(I64 val) {
    this->m_mI64 = val;
}
void StructOK1::setmI8(I8 val) {
    this->m_mI8 = val;
}
void StructOK1::setmU16(U16 val) {
    this->m_mU16 = val;
}
void StructOK1::setmU32(U32 val) {
    this->m_mU32 = val;
}
void StructOK1::setmU64(U64 val) {
    this->m_mU64 = val;
}
void StructOK1::setmU8(U8 val) {
    this->m_mU8 = val;
}
void StructOK1::setm_bool(bool val) {
    this->m_m_bool = val;
}
void StructOK1::setm_string(const StructOK1::m_stringString& val) {
    this->m_m_string = val;
}
Fw::SerializeStatus StructOK1::serialize(Fw::SerializeBufferBase& buffer) const {
    Fw::SerializeStatus stat;

#if FW_SERIALIZATION_TYPE_ID
    // serialize type ID
    stat = buffer.serialize((U32)StructOK1::TYPE_ID);
#endif

    stat = buffer.serialize(this->m_mF32);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    stat = buffer.serialize(this->m_mF64);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    stat = buffer.serialize(this->m_mI16);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    stat = buffer.serialize(this->m_mI32);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    stat = buffer.serialize(this->m_mI64);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    stat = buffer.serialize(this->m_mI8);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    stat = buffer.serialize(this->m_mU16);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    stat = buffer.serialize(this->m_mU32);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    stat = buffer.serialize(this->m_mU64);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    stat = buffer.serialize(this->m_mU8);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    stat = buffer.serialize(this->m_m_bool);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    stat = buffer.serialize(this->m_m_string);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    return stat;
}

Fw::SerializeStatus StructOK1::deserialize(Fw::SerializeBufferBase& buffer) {
    Fw::SerializeStatus stat;

#if FW_SERIALIZATION_TYPE_ID
    U32 typeId;

    stat = buffer.deserialize(typeId);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }

    if (typeId != StructOK1::TYPE_ID) {
        return Fw::FW_DESERIALIZE_TYPE_MISMATCH;
    }
#endif

    stat = buffer.deserialize(this->m_mF32);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    stat = buffer.deserialize(this->m_mF64);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    stat = buffer.deserialize(this->m_mI16);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    stat = buffer.deserialize(this->m_mI32);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    stat = buffer.deserialize(this->m_mI64);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    stat = buffer.deserialize(this->m_mI8);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    stat = buffer.deserialize(this->m_mU16);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    stat = buffer.deserialize(this->m_mU32);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    stat = buffer.deserialize(this->m_mU64);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    stat = buffer.deserialize(this->m_mU8);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    stat = buffer.deserialize(this->m_m_bool);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    stat = buffer.deserialize(this->m_m_string);
    if (stat != Fw::FW_SERIALIZE_OK) {
        return stat;
    }
    return stat;
}

#if FW_SERIALIZABLE_TO_STRING  || BUILD_UT

void StructOK1::toString(Fw::StringBase& text) const {

    static const char * formatString =
       "("
       "mF32 = %g, "
       "mF64 = %g, "
       "mI16 = %d, "
       "mI32 = %d, "
       "mI64 = %ld, "
       "mI8 = %d, "
       "mU16 = %u, "
       "mU32 = %u, "
       "mU64 = %lu, "
       "mU8 = %u, "
       "m_bool = %s, "
       "m_string = %s"
       ")";

    // declare strings to hold any serializable toString() arguments


    char outputString[FW_SERIALIZABLE_TO_STRING_BUFFER_SIZE];
    (void)snprintf(outputString,FW_SERIALIZABLE_TO_STRING_BUFFER_SIZE,formatString
       ,this->m_mF32
       ,this->m_mF64
       ,this->m_mI16
       ,this->m_mI32
       ,this->m_mI64
       ,this->m_mI8
       ,this->m_mU16
       ,this->m_mU32
       ,this->m_mU64
       ,this->m_mU8
       ,this->m_m_bool?"T":"F"
       ,this->m_m_string.toChar()
    );
    outputString[FW_SERIALIZABLE_TO_STRING_BUFFER_SIZE-1] = 0; // NULL terminate

    text = outputString;
}

#endif

#ifdef BUILD_UT
    std::ostream& operator<<(std::ostream& os, const StructOK1& obj) {
        Fw::EightyCharString str;
        obj.toString(str);
        os << str.toChar();
        return os;
    }
#endif
