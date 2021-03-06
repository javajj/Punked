package jj.com.dojo.punk.data.mapper

interface BaseMapperRepository<E, D> {

    fun transform(type: E): D

    fun transformToRepository(type: D): E

}
