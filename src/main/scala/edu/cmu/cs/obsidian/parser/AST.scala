package edu.cmu.cs.obsidian.parser

import scala.util.parsing.input.{NoPosition, Position}
import edu.cmu.cs.obsidian.lexer.Token

sealed abstract class AST() {
    var loc: Position = NoPosition
    def setLoc(t: Token): this.type = { loc = t.pos; this }
    def setLoc(ast: AST): this.type = { loc = ast.loc; this }
}
sealed abstract class Statement() extends AST

/* All expressions are statements. We relegate the pruning of expressions
 * that don't have effects to a later analysis */
sealed abstract class Expression() extends Statement
sealed abstract class Declaration() extends AST
sealed abstract class AstType() extends AST

case class Identifier(name: String) extends AST

/* Expressions */
case class Variable(x: Identifier) extends Expression
case class NumLiteral(value: Int) extends Expression
case class StringLiteral(value: String) extends Expression
case class TrueLiteral() extends Expression
case class FalseLiteral() extends Expression
case class This() extends Expression
case class Conjunction(e1: Expression, e2: Expression) extends Expression
case class Disjunction(e1: Expression, e2: Expression) extends Expression
case class LogicalNegation(e: Expression) extends Expression
case class Add(e1: Expression, e2: Expression) extends Expression
case class Subtract(e1: Expression, e2: Expression) extends Expression
case class Divide(e1: Expression, e2: Expression) extends Expression
case class Multiply(e1: Expression, e2: Expression) extends Expression
case class Equals(e1: Expression, e2: Expression) extends Expression
case class GreaterThan(e1: Expression, e2: Expression) extends Expression
case class GreaterThanOrEquals(e1: Expression, e2: Expression) extends Expression
case class LessThan(e1: Expression, e2: Expression) extends Expression
case class LessThanOrEquals(e1: Expression, e2: Expression) extends Expression
case class NotEquals(e1: Expression, e2: Expression) extends Expression
case class Dereference(e: Expression, f: Identifier) extends Expression
case class LocalInvocation(name: Identifier, args: Seq[Expression]) extends Expression
case class Invocation(recipient: Expression, name: Identifier, args: Seq[Expression]) extends Expression
case class Construction(name: Identifier, args: Seq[Expression]) extends Expression

/* statements and control flow constructs */
case class VariableDecl(typ: AstType, varName: Identifier) extends Statement
case class VariableDeclWithInit(typ: AstType, varName: Identifier, e: Expression) extends Statement
case class Return() extends Statement
case class ReturnExpr(e: Expression) extends Statement
case class Transition(newStateName: Identifier, updates: Seq[(Variable, Expression)]) extends Statement
case class Assignment(assignTo: Expression, e: Expression) extends Statement
case class Throw() extends Statement
case class If(eCond: Expression, s: Seq[Statement]) extends Statement
case class IfThenElse(eCond: Expression, s1: Seq[Statement], s2: Seq[Statement]) extends Statement
case class TryCatch(s1: Seq[Statement], s2: Seq[Statement]) extends Statement
case class Switch(e: Expression, cases: Seq[SwitchCase]) extends Statement
case class SwitchCase(stateName: Identifier, body: Seq[Statement]) extends AST

sealed abstract class TypeModifier() extends AST
case class IsReadOnly() extends TypeModifier
case class IsBorrowed() extends TypeModifier
case class IsRemote() extends TypeModifier

case class AstIntType() extends AstType
case class AstBoolType() extends AstType
case class AstStringType() extends AstType
case class AstContractType(modifiers: Seq[TypeModifier], name: Identifier) extends AstType
case class AstStateType(modifiers: Seq[TypeModifier],
                        contractName: Identifier,
                        stateName: Identifier) extends AstType

/* Declarations */
case class TypeDecl(name: Identifier, typ: AstType) extends Declaration

case class Field(typ: AstType, fieldName: Identifier) extends Declaration

case class Constructor(name: Identifier,
                       args: Seq[VariableDecl],
                       body: Seq[Statement]) extends Declaration
case class Func(name: Identifier,
                args: Seq[VariableDecl],
                retType: Option[AstType],
                body: Seq[Statement]) extends Declaration
case class Transaction(name: Identifier,
                       args: Seq[VariableDecl],
                       retType: Option[AstType],
                       ensures: Seq[Ensures],
                       body: Seq[Statement]) extends Declaration
case class State(name: Identifier, declarations: Seq[Declaration]) extends Declaration

case class Ensures(expr: Expression) extends AST

sealed abstract class ContractModifier() extends AST
case class IsOwned() extends ContractModifier
case class IsShared() extends ContractModifier
case class IsMain() extends ContractModifier

case class Import(name: String) extends AST

case class Contract(mod: Option[ContractModifier],
                    name: Identifier,
                    declarations: Seq[Declaration]) extends Declaration

/* Program */
case class Program(imports: Seq[Import], contracts: Seq[Contract]) extends AST