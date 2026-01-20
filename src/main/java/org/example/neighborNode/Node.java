package org.example.neighborNode;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Node {
    private String start;
    private String end;


    /* Node.code 값만 사용하여 집합의 합집합 교집합을 구함 >> Main.v3 */
//    private String code;
//    private String name;
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Node item = (Node) o;
//        //return Objects.equals(code, item.code) && Objects.equals(name, item.name);
//        return Objects.equals(code, item.code);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(code, name);
//    }
}
