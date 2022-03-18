// priority: 0

let Lupus590 = {};
Lupus590.Serialise = function(obj, indent){
	let maxIndent = 0;

	if (indent !== undefined && indent !== null){
		indent = indent;
	}else{
		indent = 0;
	}

	if (indent > maxIndent){
		if (typeof(obj) === 'string'){
			return '"'+obj+'"';
		}else if (typeof(obj) === 'boolean' || typeof(obj) === 'number'){
			return obj;
		}else if (typeof(obj) === 'function'){
			return "function";
		}else if (obj === null || obj === undefined){
			return "null";
		}else if (typeof(obj) === 'object' && obj !== null){
			return "max indent object";
		}else{
			console.info('unhandled case');
			throw {message: 'unhandled case'};
		}
	}

	if (typeof(obj) === 'string'){
		return '"'+obj+'"';
	}else if (typeof(obj) === 'boolean' || typeof(obj) === 'number'){
		return obj;
	}else if (typeof(obj) === 'function'){
		return "function";
	}else if (obj === null || obj === undefined){
		return "null";
	}else if (typeof(obj) === 'object' && obj !== null){
		let s = "{\n";
		let pad = "";
		indent = indent+1;
		pad = "    ".repeat(indent);
		Object.keys(obj).forEach(key => {
			try {
				s = s+pad+key+" = "+Lupus590.Serialise(obj[key], indent)+",\n";
			} catch (error) {
				s = s+pad+key+" = error, \n";
			}
		});
		indent = indent-1;
		if (indent > 0){
		pad = "    ".repeat(indent);
		}
		else{
		pad = "";
		}
		s = s+pad+"}";
		return s;
	}else{
		console.info('unhandled case');
		throw {message: 'unhandled case'};
	}
};

onEvent('block.break', event => {
	// TODO: allow real players to dig still
	//console.log("event.player = "+Lupus590.Serialise(event.player));

    //event.player.name == the UUID
	//event.block.entity.serializeNBT().Owner.Name == the UUID

	// TODO: turtles with the same owner can't mine each other

	if (event.block.entity && event.block.entity.serializeNBT() && event.block.entity.serializeNBT().Owner){
		if (event.block.entity.serializeNBT().Owner.name !== event.player.name){
			//event.cancel();
		}
	}
});
