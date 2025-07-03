package com.bryanmzili.DevLab.service;

import com.bryanmzili.DevLab.data.Partida;
import com.bryanmzili.DevLab.data.PartidaRepository;
import com.bryanmzili.DevLab.data.PartidaViewDTO;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
public class PartidaService {

    @Autowired
    PartidaRepository partidaRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public boolean salvarPartida(Partida partida) {
        partidaRepository.save(partida);
        return true;
    }

    public List<PartidaViewDTO> historico(String idJogador) {

        Criteria jogador1Valido = new Criteria().andOperator(
                Criteria.where("jogador1").ne(null),
                Criteria.where("jogador1").ne(""),
                Criteria.where("jogador1").regex("^[a-fA-F0-9]{24}$")
        );

        Criteria jogador2Valido = new Criteria().andOperator(
                Criteria.where("jogador2").ne(null),
                Criteria.where("jogador2").ne(""),
                Criteria.where("jogador2").regex("^[a-fA-F0-9]{24}$")
        );

        Criteria vencedorValido = new Criteria().andOperator(
                Criteria.where("vencedor").ne(null),
                Criteria.where("vencedor").ne(""),
                Criteria.where("vencedor").regex("^[a-fA-F0-9]{24}$")
        );

        Criteria matchCriteria = new Criteria().andOperator(
                new Criteria().orOperator(
                        Criteria.where("jogador1").is(idJogador),
                        Criteria.where("jogador2").is(idJogador)
                ),
                jogador1Valido,
                jogador2Valido,
                vencedorValido
        );

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(matchCriteria),
                Aggregation.addFields()
                        .addFieldWithValue("jogador1ObjId", ConvertOperators.ToObjectId.toObjectId("$jogador1"))
                        .build(),
                Aggregation.addFields()
                        .addFieldWithValue("jogador2ObjId", ConvertOperators.ToObjectId.toObjectId("$jogador2"))
                        .build(),
                Aggregation.addFields()
                        .addFieldWithValue("vencedorObjId", ConvertOperators.ToObjectId.toObjectId("$vencedor"))
                        .build(),
                Aggregation.lookup("usuarios", "jogador1ObjId", "_id", "jogador1Info"),
                Aggregation.lookup("usuarios", "jogador2ObjId", "_id", "jogador2Info"),
                Aggregation.lookup("usuarios", "vencedorObjId", "_id", "vencedorInfo"),
                Aggregation.project()
                        .and("jogador1Info.usuario").arrayElementAt(0).as("jogador1")
                        .and("jogador2Info.usuario").arrayElementAt(0).as("jogador2")
                        .and("vencedorInfo.usuario").arrayElementAt(0).as("vencedor")
                        .and("data").as("data")
                        .and("status").as("status")
        );

        AggregationResults<PartidaViewDTO> results = mongoTemplate.aggregate(agg, "partidas", PartidaViewDTO.class);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        return results.getMappedResults().stream().map(dto -> {
            try {
                Object dataObj = dto.getData();

                if (dataObj instanceof LocalDateTime ldt) {
                    dto.setData(ldt.format(formatter));
                } else if (dataObj instanceof java.util.Date date) {
                    LocalDateTime ldt = date.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();
                    dto.setData(ldt.format(formatter));
                } else if (dataObj instanceof String s) {
                    LocalDateTime ldt = LocalDateTime.parse(s);
                    dto.setData(ldt.format(formatter));
                } else {
                    dto.setData("Data inválida");
                }
            } catch (Exception e) {
                dto.setData("Data inválida");
            }
            return dto;
        }).toList();

    }

}
