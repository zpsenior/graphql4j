package graphql4j.engine;

import java.io.PrintWriter;

import graphql4j.exception.ExecuteException;
import graphql4j.operation.GraphQL;
import graphql4j.operation.GraphQLBuilder;
import graphql4j.type.GraphQLSchema;
import graphql4j.type.GraphQLSchemaLoader;

public class GraphQLInterpretor {

	private GraphQLSchema schema;
	private GraphQLExecute exec;
	private GraphQLBuilder builder;
	private GraphQL gql;
	
	private Class<?> clsQuery;
	private Class<?> clsMutation;
	
	public GraphQLInterpretor(Class<?> clsQuery, Class<?> clsMutation)throws Exception{
		this.clsQuery = clsQuery;
		this.clsMutation = clsMutation;
		GraphQLSchemaLoader loader = new GraphQLSchemaLoader();
		schema = loader.load(new Class[] {clsQuery, clsMutation});
		builder = new GraphQLBuilder(schema);
		exec = new GraphQLExecute(schema);
	}
	
	public void bindGraphQLResource(String gqlContent)throws Exception{
		gql = builder.build(gqlContent);
	}
	
	public GraphQL.Operation getOperation(String action)throws Exception{
		GraphQL.Operation opt = gql.getOperation(action);
		if(opt == null){
			throw new ExecuteException("no.find.opt.name:" + action);
		}
		return opt;
	}
	
	public void interpret(GraphQL.Operation opt, ParamValueBinder binder, PrintWriter pw)throws Exception{
		opt.clearValues();
		opt.bindValues(binder);
		if(opt.getOperationType() == GraphQL.TYPE_MUTATION) {
			exec.execute(opt, clsMutation.newInstance(), schema.getMutationType(), pw);
		}else if(opt.getOperationType() == GraphQL.TYPE_QUERY) {
			exec.execute(opt, clsQuery.newInstance(), schema.getQueryType(), pw);
		}
		pw.flush();
	}

}
