/*
 * Grakn - A Distributed Semantic Database
 * Copyright (C) 2016-2018 Grakn Labs Limited
 *
 * Grakn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Grakn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Grakn. If not, see <http://www.gnu.org/licenses/agpl.txt>.
 */

package ai.grakn.graql.internal.query.aggregate;

import ai.grakn.graql.Match;
import ai.grakn.graql.Var;
import ai.grakn.graql.admin.Answer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Aggregate that finds median of a {@link Match}.
 */
class MedianAggregate extends AbstractAggregate<Answer, Optional<Number>> {

    private final Var varName;

    MedianAggregate(Var varName) {
        this.varName = varName;
    }

    @Override
    public Optional<Number> apply(Stream<? extends Answer> stream) {
        List<Number> results = stream
                .map(result -> ((Number) result.get(varName).asAttribute().getValue()))
                .sorted()
                .collect(toList());

        int size = results.size();
        int halveFloor = Math.floorDiv(size - 1, 2);
        int halveCeiling = (int) Math.ceil((size - 1) / 2.0);

        if (size == 0) {
            return Optional.empty();
        } else if (size % 2 == 1) {
            // Take exact middle result
            return Optional.of(results.get(halveFloor));
        } else {
            // Take average of middle results
            return Optional.of((results.get(halveFloor).doubleValue() + results.get(halveCeiling).doubleValue()) / 2);
        }
    }

    @Override
    public String toString() {
        return "median " + varName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MedianAggregate that = (MedianAggregate) o;

        return varName.equals(that.varName);
    }

    @Override
    public int hashCode() {
        return varName.hashCode();
    }
}
